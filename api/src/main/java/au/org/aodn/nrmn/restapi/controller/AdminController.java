package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.service.ScheduledTasks;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@Tag(name = "Admin")
public class AdminController {

    @Autowired
    ScheduledTasks scheduledTasks;

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/admin/runStartupTasks")
    public void adminRunStartupTasks(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {

        if (Objects.isNull(authentication))
            return;

        var hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminRole) {
            logger.warn("Manual startup task run attempted by non-admin user: " + authentication.getName());
            return;
        }

        logger.info("Manual startup tasks run by admin: " + authentication.getName());

        scheduledTasks.runStartupTasks();

    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(path = "/v1/admin/runDailyTasks")
    public void adminRunDailyTasks(
            Authentication authentication,
            @RequestHeader(name = "Authorization") String bearerToken) {

        if (Objects.isNull(authentication))
            return;

        var hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminRole) {
            logger.warn("Manual daily task run attempted by non-admin user: " + authentication.getName());
            return;
        }

        logger.info("Manual daily tasks run by admin: " + authentication.getName());

        scheduledTasks.runDailyTasks();

    }

}