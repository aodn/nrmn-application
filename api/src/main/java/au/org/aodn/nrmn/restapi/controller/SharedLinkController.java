package au.org.aodn.nrmn.restapi.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;
import au.org.aodn.nrmn.restapi.data.repository.*;
import au.org.aodn.nrmn.restapi.dto.sharedlink.SharedLinkDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "SharedLinks")
@RequestMapping(path = "/api/v1")
public class SharedLinkController {

    @Autowired
    private SharedLinkRepository sharedLinkRepository;

    @GetMapping("/sharedLinks")
    public ResponseEntity<List<SharedLinkDto>> getSharedLinks() {
        var sharedLinks = sharedLinkRepository.findAll().stream().map(SharedLinkDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(sharedLinks);
    }

    @PostMapping("/sharedLinks")
    public ResponseEntity<SharedLink> createSharedLink() {
        SharedLink sharedLink = new SharedLink();
        sharedLink.setPublicId(UUID.randomUUID().toString());
        return ResponseEntity.ok(sharedLinkRepository.save(sharedLink));
    }
}
