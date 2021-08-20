package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.diver.DiverDto;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "diver")
@RequestMapping(path = "/api")
public class DiverController {

    @Autowired
    private DiverRepository diverRepository;

    @GetMapping("/diver/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public DiverDto findOne(@PathVariable Integer id) {
        Diver diver = diverRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return new DiverDto(diver);
    }

    @PostMapping("/diver")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> newDiver(@Valid @RequestBody DiverDto diverDto) {
        Diver diver = new Diver(diverDto.getInitials(), diverDto.getFullName());

        ValidationErrors errors = validateConstraints(diver);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Diver persistedDiver = diverRepository.save(diver);
        DiverDto persistedDiverDto = new DiverDto(persistedDiver);
        return ResponseEntity.status(HttpStatus.CREATED).body(persistedDiverDto);
    }

    @PutMapping("/diver/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> updateDiver(@PathVariable Integer id, @Valid @RequestBody DiverDto diverDto) {
        Diver diver = diverRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        diver.setInitials(diverDto.getInitials());
        diver.setFullName(diverDto.getFullName());

        ValidationErrors errors = validateConstraints(diver);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Diver persistedDiver = diverRepository.save(diver);
        DiverDto updatedDiverDto = new DiverDto(persistedDiver);
        return ResponseEntity.ok().body(updatedDiverDto);
    }


    private ValidationErrors validateConstraints(Diver diver) {
        List<ValidationError> errors = new ArrayList<>();

        List<Diver> existingDiversWithInitials = diverRepository.findByInitials(diver.getInitials());

        if(diver.getDiverId() != null) {
            existingDiversWithInitials = existingDiversWithInitials
                    .stream().filter(d -> !d.getDiverId().equals(diver.getDiverId())).collect(Collectors.toList());
        }

        if (!existingDiversWithInitials.isEmpty()) {
            errors.add(new ValidationError("Diver", "initials", diver.getInitials(),
                    "A diver with these initials already exists."));
        }

        List<Diver> existingDiversWithSameName = diverRepository.findByFullName(diver.getFullName());

        if(diver.getDiverId() != null) {
            existingDiversWithSameName = existingDiversWithSameName
                    .stream().filter(d -> !d.getDiverId().equals(diver.getDiverId())).collect(Collectors.toList());
        }

        if (!existingDiversWithSameName.isEmpty()) {
            errors.add(new ValidationError("Diver", "fullName", diver.getInitials(),
                    "A diver with the same name already exists."));
        }

        return new ValidationErrors(errors);
    }

}
