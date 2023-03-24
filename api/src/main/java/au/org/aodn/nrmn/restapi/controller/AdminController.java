package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.SecRole;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.repository.SecRoleRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.enums.SecRoleName;
import au.org.aodn.nrmn.restapi.service.ScheduledTasks;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static boolean isAdmin(Authentication authentication) {

        if (Objects.isNull(authentication))
            return false;

        var hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (Objects.isNull(hasAdminRole)) {
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

        var userList = userRepository.findAll().stream().sorted(Comparator.comparing(SecUser::getEmail)).toArray();
        return ResponseEntity.ok(userList);
    }

    // @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    // @PutMapping(path = "/v1/admin/user")
    // public ResponseEntity<?> setUser(
    //         Authentication authentication,
    //         @RequestHeader(name = "Authorization") String bearerToken,
    //         @RequestBody SecUserPost user) {

    //     if (!isAdmin(authentication))
    //         return ResponseEntity.badRequest().body("Unauthorized");

    //     var userRole = roleRepository.findByName(SecRoleName.ROLE_DATA_OFFICER).get();
    //     var adminRole = roleRepository.findByName(SecRoleName.ROLE_ADMIN).get();

    //     Set<SecRole> roles = user.getNewRoles().contains("ROLE_ADMIN") ? Set.of(userRole, adminRole)
    //             : user.getNewRoles().contains("ROLE_DATA_OFFICER") ? Set.of(userRole) : Set.of();
    //     var updatedUser = userRepository.getReferenceById(user.getUserId());
    //     updatedUser.setFullName(user.getFullName());
    //     updatedUser.setEmail(user.getEmail());
    //     updatedUser.setRoles(roles);
    //     updatedUser = userRepository.save(updatedUser);
    //     return ResponseEntity.ok(updatedUser);
    // }

}