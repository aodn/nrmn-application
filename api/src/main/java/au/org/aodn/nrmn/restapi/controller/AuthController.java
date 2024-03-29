package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.data.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import au.org.aodn.nrmn.restapi.util.LogInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "User Authentication")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecRoleRepository roleRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserActionAuditRepository userAuditRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SecUserRepository userRepository;

    @Value("${app.api.version}")
    private String appVersion;

    @Value("${app.features}")
    String[] features;

    @Value("${aggrid.license}")
    String gridLicence;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/auth/signout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> logOutUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {
        long timeStamp = System.currentTimeMillis();
        userAuditRepo.save(
                new UserActionAudit(
                        "Logout",
                        "logout attempt for username: " + authentication.getName()
                                + " token: " + bearerToken));
        return tokenProvider.getAuthorizationBearer(bearerToken).map(token -> {
            if (!SecUserRepository.blackListedTokenPresent(token)) {
                SecUserRepository.addBlackListedToken(timeStamp, token);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/v1/auth/signin", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info(LogInfo.withContext("login attempt"));
        userAuditRepo.save(
                new UserActionAudit("signin", "login attempt for username: " + loginRequest.getUsername()));
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            var user = userRepository.findByEmail(loginRequest.getUsername()).get();
            var expires = user.getExpires() != null ? user.getExpires() : LocalDateTime.MAX;
            if (expires.isBefore(LocalDateTime.now())) {
                return ResponseEntity.ok(new JwtAuthenticationResponse());
            }
        }
        var jwt = tokenProvider.generateToken(authentication);
        if (SecUserRepository.blackListedTokenPresent(jwt)) {
            SecUserRepository.removeBlackListedToken(jwt);
        }
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, gridLicence, features));
    }

    @PostMapping(path = "/v1/auth/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> updatePassword(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info(LogInfo.withContext("login attempt"));
        userAuditRepo.save(
                new UserActionAudit("update", "update password attempt for username: " + loginRequest.getUsername()));
        var newPassword = loginRequest.getNewPassword();
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("New password is less than 8 characters");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Unauthorized");
        }
        var user = userRepository.findByEmail(loginRequest.getUsername()).get();
        user.setExpires(null);
        user.setHashedPassword(passwordEncoder.encode(loginRequest.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/v1/auth/hash", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HashMap<String, String>> registerUser(@Valid @RequestBody String password) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("hash", passwordEncoder.encode(password));
        return ResponseEntity.ok(payload);

    }

    @GetMapping({ "/v1/version", "/v2/version" })
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok(appVersion);

    }
}