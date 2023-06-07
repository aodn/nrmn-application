package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.SecRole;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.dto.auth.SecUserDto;
import au.org.aodn.nrmn.restapi.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.enums.SecUserStatus;
import au.org.aodn.nrmn.restapi.service.ScheduledTasks;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "Admin")
public class AdminController {

    @Autowired
    ScheduledTasks scheduledTasks;

    @Autowired
    SecUserRepository userRepository;

    @Autowired
    SecRoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static boolean isAdmin(Authentication authentication) {

        if (Objects.isNull(authentication))
            return false;

        var hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminRole) {
            logger.warn("Admin action attempted by non-admin user: " + authentication.getName());
            return false;
        }

        return true;
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/admin/runStartupTasks")
    public ResponseEntity<?> adminRunStartupTasks(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {

        if (!isAdmin(authentication))
            return ResponseEntity.badRequest().body("Unauthorized");

        logger.info("Manual startup tasks run by admin: " + authentication.getName());

        scheduledTasks.runStartupTasks();

        return ResponseEntity.ok().build();
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/admin/runDailyTasks")
    public ResponseEntity<?> adminRunDailyTasks(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {

        if (!isAdmin(authentication))
            return ResponseEntity.badRequest().body("Unauthorized");

        logger.info("Manual daily tasks run by admin: " + authentication.getName());

        scheduledTasks.runDailyTasks();

        return ResponseEntity.ok().build();
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(path = "/v1/admin/users")
    public ResponseEntity<?> getUsers(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {

        if (!isAdmin(authentication))
            return ResponseEntity.badRequest().body("Unauthorized");

        var userList = userRepository.findAll().stream()
                .sorted(Comparator.comparing(SecUser::getEmail))
                .map(u -> new SecUserDto(u, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userList);
    }

    private SecUserDto persistUserDto(SecUser updatedUser, SecUserDto user) {
        var temporaryPassword = "";
        try {
            var isNewUser = Objects.isNull(user.getUserId());

            var adminRole = roleRepository.findByName(SecRoleName.ROLE_ADMIN).get();
            var dataOfficerRole = roleRepository.findByName(SecRoleName.ROLE_DATA_OFFICER).get();
            var surveyEditorRole = roleRepository.findByName(SecRoleName.ROLE_SURVEY_EDITOR).get();

            if (Objects.nonNull(updatedUser.getRoles())) {
                updatedUser.getRoles().clear();

                if(user.getRoles().contains(SecRoleName.ROLE_ADMIN.toString())) {
                    updatedUser.getRoles().addAll(Set.of(dataOfficerRole, adminRole));
                }
                else if(user.getRoles().contains(SecRoleName.ROLE_DATA_OFFICER.toString())) {
                    updatedUser.getRoles().add(dataOfficerRole);
                }
                else if(user.getRoles().contains(SecRoleName.ROLE_SURVEY_EDITOR.toString())) {
                    updatedUser.getRoles().add(surveyEditorRole);
                }
            }
            else {
                updatedUser.setRoles(Set.<SecRole>of(dataOfficerRole));
            }

            if (isNewUser || user.isResetPassword()) {
                temporaryPassword = RandomStringUtils.random(8, 0, 0, true, true, null, new SecureRandom());
                updatedUser.setExpires(LocalDateTime.of(2020, 01, 01, 0, 0));
                updatedUser.setHashedPassword(passwordEncoder.encode(temporaryPassword));
            }
            updatedUser.setFullName(user.getFullName());
            updatedUser.setEmail(user.getEmail());
            var isActiveUser = (isNewUser || user.getRoles().size() > 0);
            updatedUser.setStatus(isActiveUser ? SecUserStatus.ACTIVE : SecUserStatus.DEACTIVATED);
            updatedUser = userRepository.save(updatedUser);
        } catch (DataIntegrityViolationException ex) {
            var ret = new SecUserDto(updatedUser, null);
            ret.setError("Email must be unique");
            return ret;
        }
        return new SecUserDto(updatedUser, temporaryPassword);
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/admin/user")
    public ResponseEntity<?> addUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken,
            @RequestBody SecUserDto user) {

        if (!isAdmin(authentication))
            return ResponseEntity.badRequest().body("Unauthorized");

        var updatedUser = new SecUser();

        return ResponseEntity.ok(persistUserDto(updatedUser, user));
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PutMapping(path = "/v1/admin/user")
    public ResponseEntity<?> updateUser(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken,
            @RequestBody SecUserDto user) {

        if (!isAdmin(authentication))
            return ResponseEntity.badRequest().body("Unauthorized");

        var thisUser = userRepository.findByEmail(authentication.getName()).get();

        var userId = user.getUserId();
        var thisUserId = thisUser.getUserId();
        if(userId.equals(thisUserId)){
            var ret = new SecUserDto(thisUser, null);
            ret.setError("Cannot update current user");
            return ResponseEntity.ok(ret);
        }

        var updatedUser = userRepository.getReferenceById(user.getUserId());

        return ResponseEntity.ok(persistUserDto(updatedUser, user));
    }

}