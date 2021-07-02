package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.service.UserService;
import au.org.aodn.nrmn.restapi.util.LogInfo;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(path = "/api/auth")
@Tag(name = "authorisation")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecRoleRepository roleRepository;

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    UserActionAuditRepository userAuditRepo;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(path = "/signout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> logOutUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {
        val timeStamp = System.currentTimeMillis();
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
        }).orElseGet(() ->  ResponseEntity.noContent().build());


    }

    @PostMapping(path = "/signin", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info(LogInfo.withContext("login attempt"));
        userAuditRepo.save(
                new UserActionAudit("signin", "login attempt for username: " + loginRequest.getUsername()));
        val authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        val jwt = tokenProvider.generateToken(authentication);
        if (SecUserRepository.blackListedTokenPresent(jwt)) {
            SecUserRepository.removeBlackListedToken(jwt);
        }
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequestDto) {

        userAuditRepo.save(new UserActionAudit("registerUser", signUpRequestDto.toString()));

        val validedUSer = userService.createUser(signUpRequestDto);
        ResponseEntity<Object> response = validedUSer.fold(
                (err) -> {
                    logger.info("Error while signup");
                    return ResponseEntity.unprocessableEntity().body(err);
                }
                , (user) -> {
                    logger.info("Successful signup");
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentContextPath().path("/api/users/{id}")
                            .buildAndExpand(user.getUserId()).toUri();
                    return ResponseEntity.created(location).body(user);
                });
        return response;
    }

}