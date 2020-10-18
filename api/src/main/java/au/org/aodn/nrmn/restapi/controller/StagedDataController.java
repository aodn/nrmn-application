package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.dto.stage.FileUpload;
import au.org.aodn.nrmn.restapi.dto.stage.UploadResponse;
import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAuditEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobEntityRepository;
import au.org.aodn.nrmn.restapi.repository.StagedSurveyEntityRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditEntityRepository;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.model.SheetWithHeader;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import cyclops.companion.Monoids;
import cyclops.control.Maybe;
import cyclops.control.Validated;
import cyclops.data.Seq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jdk.nashorn.internal.runtime.regexp.joni.Option;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/stage")
public class StagedDataController {

    @Autowired
    SpreadSheetService sheetService;
    @Autowired
    StagedSurveyEntityRepository stagedSurveyRepo;
    @Autowired
    UserActionAuditEntityRepository userAuditRepo;

    @Autowired
    private StagedJobEntityRepository jobRepo;

    @PostMapping("/upload")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<UploadResponse> uploTadFiles(
            @RequestParam("withInvertSize") Boolean withInvertSize,
            @RequestParam("file") MultipartFile file, Authentication authentication) {

        userAuditRepo.save(
                new UserActionAuditEntity(
                        "stage/upload",
                        "upload excel file attempt for username: " + authentication.getName()
                                + " token: " + file.getOriginalFilename())
        );

        val validationHelper = new ValidatorHelpers();
        val validatedSheet =
                Maybe.attempt(() ->
                        sheetService
                                .validatedExcelFile(
                                        file.getOriginalFilename() + "-" + System.currentTimeMillis(),
                                        file,
                                        withInvertSize)
                ).orElseGet(() ->
                        Validated.invalid(new ErrorInput("No File provided", "upload"))
                );

        List<ErrorInput> errors = validationHelper.toErrorList(validatedSheet);

        return validatedSheet.fold(
                err -> ResponseEntity.unprocessableEntity().body(new UploadResponse(Optional.empty(), errors)),
                sheet -> {
                    val stagedSurveyToSave = sheetService.sheets2Staged(sheet);
                    val stagedJob = jobRepo.save(
                            new StagedJobEntity(
                                    sheet.getFileId(),
                                    StatusJobType.PENDING,
                                    SourceJobType.FILE, new HashMap<>()
                            ));
                    stagedSurveyRepo.saveAll(stagedSurveyToSave.stream().map(s -> {
                        s.setStagedJob(stagedJob);
                        return s;
                    }).collect(Collectors.toList()));
                    val filesResult = new FileUpload(sheet.getFileId(), stagedSurveyToSave.size());
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new UploadResponse(Optional.of(filesResult), Collections.emptyList()));
                });
    }
}
