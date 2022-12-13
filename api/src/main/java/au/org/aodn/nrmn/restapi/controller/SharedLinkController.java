package au.org.aodn.nrmn.restapi.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import au.org.aodn.nrmn.restapi.data.model.SharedLink;
import au.org.aodn.nrmn.restapi.data.repository.*;

public class SharedLinkController {

    @Autowired
    private SharedLinkRepository sharedLinkRepository;

    @GetMapping("/sharedLinks")
    public ResponseEntity<List<SharedLink>> getSharedLinks() {
        return ResponseEntity.ok(sharedLinkRepository.findAll());
    }

    @PostMapping("/sharedLinks")
    public ResponseEntity<SharedLink> createSharedLink() {
        SharedLink sharedLink = new SharedLink();
        sharedLink.setPublicId(UUID.randomUUID().toString());
        return ResponseEntity.ok(sharedLinkRepository.save(sharedLink));
    }
}
