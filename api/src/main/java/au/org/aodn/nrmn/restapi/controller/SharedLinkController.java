package au.org.aodn.nrmn.restapi.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.SharedLinkRepository;
import au.org.aodn.nrmn.restapi.dto.sharedlink.SharedLinkDto;
import au.org.aodn.nrmn.restapi.enums.SharedLinkType;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;
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
    private MaterializedViewService materializedViewService;

    @Autowired
    private S3IO s3IO;

    @Autowired
    private SecUserRepository userRepo;

    @GetMapping("/sharedLinks")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<List<SharedLinkDto>> getSharedLinks() {
        var sharedLinks = sharedLinkRepository.findAll().stream().map(SharedLinkDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(sharedLinks);
    }

    @DeleteMapping("/sharedLink/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<String> deleteLink(@PathVariable Long id) {
        try {
            var link = sharedLinkRepository.getReferenceById(id);
            s3IO.deleteS3Link(link.getTargetUrl());
            sharedLinkRepository.delete(link);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/sharedLink")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> createSharedLink(Authentication authentication,
            @RequestBody SharedLinkDto sharedLinkDto) {
        try {

            var currentLinkTypes = sharedLinkRepository.findAllDistinctLinkTypes();
            if (!currentLinkTypes.contains(sharedLinkDto.getContent())) {
                materializedViewService.uploadMaterializedView(sharedLinkDto.getContent().toLowerCase());
            }

            var expires = LocalDate.parse(sharedLinkDto.getExpires()).atStartOfDay();
            var sharedLink = new SharedLink();
            var user = userRepo.findByEmail(authentication.getName());
            sharedLink.setUser(user.get());
            sharedLink.setReceipient(sharedLinkDto.getRecipient());
            sharedLink.setLinkType(SharedLinkType.valueOf(sharedLinkDto.getContent().toUpperCase()));
            sharedLink.setTargetUrl(s3IO.createS3Link(sharedLinkDto.getContent().toLowerCase(), expires));
            sharedLink.setExpires(expires);
            sharedLinkRepository.save(sharedLink);
            return ResponseEntity.ok(sharedLink.getTargetUrl());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
