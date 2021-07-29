package au.org.aodn.nrmn.restapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
@RequestMapping(path = "/api/config")
@Tag(name = "config")
public class ConfigController {

    @GetMapping(path = "aggrid")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<HashMap<String, String>> getAGGridConfig(@Value("${aggrid.license}") String license) {

        HashMap<String, String> payload = new HashMap<>();

        if(!StringUtils.isEmpty(license)) {
            payload.put("license", license);
        }

        return ResponseEntity.ok().body(payload);

    }

}
