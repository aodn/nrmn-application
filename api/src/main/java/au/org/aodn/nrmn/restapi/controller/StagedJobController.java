package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.dto.stage.FileUpload;
import au.org.aodn.nrmn.restapi.dto.stage.UploadResponse;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/stage")
@Tag(name = "staged jobs")
public class StagedJobController {

    @Autowired
    SpreadSheetService sheetService;

    @Autowired

    StagedRowRepository stagedRowRepo;

    @Autowired
    UserActionAuditRepository userAuditRepo;

    @Autowired
    ProgramRepository programRepo;

    @Autowired
    private StagedJobRepository jobRepo;

    @PostMapping("/upload")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<UploadResponse> uploadFile(
            @RequestParam("withInvertSize") Boolean withInvertSize,
            @RequestParam("programiId") Integer programId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
       userAuditRepo.save(
            new UserActionAudit(
                "stage/upload",
                "upload excel file attempt for username: " + authentication.getName()
                    + " token: " + file.getOriginalFilename())
        );

        val validationHelper = new ValidatorHelpers();
        val validatedSheet =
                sheetService
                        .validatedExcelFile(
                                file.getOriginalFilename() + "-" + System.currentTimeMillis(),
                                file,
                                withInvertSize);

        List<ErrorInput> errors = validationHelper.toErrorList(validatedSheet);
        val programOpt = programRepo.findById(programId);
        if (!programOpt.isPresent())
            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new UploadResponse(Optional.empty(),
                            Stream.of(new ErrorInput("Program Not found","program")).collect(Collectors.toList())));

        return validatedSheet.fold(
                err -> ResponseEntity.unprocessableEntity().

                        body(new UploadResponse(Optional.empty(), errors)),
                sheet ->

                {
                    val stagedRowToSave = sheetService.sheets2Staged(sheet);
                    val stagedJob = jobRepo.save(
                            StagedJob.builder()
                                    .source(SourceJobType.FILE)
                                    .reference(sheet.getFileId())
                                    .status(StatusJobType.PENDING)
                                    .program(programOpt.get())
                                    .build());
                    stagedRowRepo.saveAll(stagedRowToSave.stream().map(s -> {
                      s.setStagedJob(stagedJob);
                        return s;
                    })
                    .collect(Collectors.toList()));
                val filesResult = new FileUpload(sheet.getFileId(), stagedRowToSave.size());
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new UploadResponse(Optional.of(filesResult), Collections.emptyList()));
            });
    }
}
