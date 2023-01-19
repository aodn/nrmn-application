package au.org.aodn.nrmn.restapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.SharedLinkRepository;
import au.org.aodn.nrmn.restapi.dto.sharedlink.SharedLinkDto;
import au.org.aodn.nrmn.restapi.dto.sharedlink.SharedLinkPutDto;
import au.org.aodn.nrmn.restapi.service.upload.SharedLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "SharedLinks")
@RequestMapping(path = "/api/v1")
public class SharedLinkController {

    @Autowired
    private SharedLinkRepository sharedLinkRepository;

    @Autowired
    private SharedLinkService sharedLinkService;

    @Autowired
    private SecUserRepository userRepo;

    private static Logger logger = LoggerFactory.getLogger(SharedLinkController.class);

    @GetMapping("/sharedLinks")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<List<SharedLinkDto>> getSharedLinks() {
        var sharedLinks = sharedLinkRepository.findAll(Sort.by(Direction.DESC, "created")).stream().map(SharedLinkDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(sharedLinks);
    }

    @DeleteMapping("/sharedLink/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<String> deleteLink(@PathVariable Long id) {
        try {
            sharedLinkService.expireLink(id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to delete shared link");
        }
    }

    @PutMapping("/sharedLink")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> createSharedLink(Authentication authentication,
            @RequestBody SharedLinkPutDto sharedLinkDto) {
        try {
            var user = userRepo.findByEmail(authentication.getName());
            var links = sharedLinkService.createLinks(user.get(), sharedLinkDto.getRecipient(), sharedLinkDto.getExpires(), sharedLinkDto.getEndpoints());
            return ResponseEntity.ok(links);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ResponseEntity.internalServerError().body("Failed to generate shared link");
    }
}
