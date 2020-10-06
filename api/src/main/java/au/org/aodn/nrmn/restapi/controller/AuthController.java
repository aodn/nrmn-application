package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.repository.SecUserEntityRepository;
import au.org.aodn.nrmn.restapi.service.UserService;
import au.org.aodn.nrmn.restapi.util.LogInfo;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAuditEntity;
import au.org.aodn.nrmn.restapi.repository.SecRoleEntityRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditEntityRepository;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;
import org.apache.tomcat.util.buf.UriUtil;
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
@CrossOrigin
@RequestMapping(path = "/api/auth")
@Tag(name = "auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecRoleEntityRepository roleRepository;

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    UserActionAuditEntityRepository userAuditRepo;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(path = "/signout", consumes = "application/json", produces = "application/json")
    public ResponseEntity logOutUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {
        val timeStamp = System.currentTimeMillis();
        userAuditRepo.save(
                new UserActionAuditEntity(
                        "Logout",
                        "logout attempt for username: " + authentication.getName()
                                + " token: " + bearerToken));
       return tokenProvider.getAuthorizationBearer(bearerToken).map(token -> {
            if (!SecUserEntityRepository.blackListedTokenPresent(token)) {
                SecUserEntityRepository.addBlackListedToken(timeStamp, token);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }).orElseGet(() ->  ResponseEntity.noContent().build());


    }

    @PostMapping(path = "/signin", consumes = "application/json", produces = "application/json")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info(LogInfo.withContext("login attempt"));
        userAuditRepo.save(
                new UserActionAuditEntity("signin", "login attempt for username: " + loginRequest.getUsername()));
        val authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        val jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity registerUser(@Valid @RequestBody SignUpRequest signUpRequestDto) {

        userAuditRepo.save(new UserActionAuditEntity("registerUser", signUpRequestDto.toString()));

        val validedUSer = userService.createUser(signUpRequestDto);
        ResponseEntity<?> response = validedUSer.fold(
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