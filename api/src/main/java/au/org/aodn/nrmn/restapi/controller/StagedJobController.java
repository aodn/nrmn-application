package au.org.aodn.nrmn.restapi.controller;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.dto.site.RowUpdateDto;
import au.org.aodn.nrmn.restapi.dto.stage.FileUpload;
import au.org.aodn.nrmn.restapi.dto.stage.JobResponse;
import au.org.aodn.nrmn.restapi.dto.stage.UploadResponse;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.StagedRowService;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;

@RestController
@RequestMapping(path = "/api/stage")
@Tag(name = "staged jobs")
public class StagedJobController {

    @Autowired
    SpreadSheetService sheetService;

    @Autowired
    StagedRowService rowService;

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    UserActionAuditRepository userAuditRepo;

    @Autowired
    ProgramRepository programRepo;

    @Autowired
    private StagedJobRepository jobRepo;

    @Autowired
    private ValidationProcess validation;

    @Autowired
    private SecUserRepository userRepo;

    @Autowired
    private StagedJobLogRepository logRepo;

    @PostMapping("/upload")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("withExtendedSizes") Boolean withExtendedSizes,
            @RequestParam("programId") Integer programId, @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/upload", "upload excel file attempt for username: "
                + authentication.getName() + " file: " + file.getOriginalFilename()));
        val programOpt = programRepo.findById(programId);
        if (!programOpt.isPresent())
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new UploadResponse(Optional.empty(),
                    Stream.of(new ErrorInput("Program Not found", "program")).collect(Collectors.toList())));
        val user = userRepo.findByEmail(authentication.getName());

        val job = StagedJob.builder().isExtendedSize(withExtendedSizes).source(SourceJobType.INGEST)
                .reference(file.getOriginalFilename()).status(StatusJobType.PENDING).program(programOpt.get())
                .creator(user.get()).build();
        jobRepo.save(job);

        val jobLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis()))
                .eventType(StagedJobEventType.UPLOADED).stagedJob(job)
                .details(file.getOriginalFilename() + " uploaded by:" + authentication.getName()).build();

        logRepo.save(jobLog);

        val validatedSheet = sheetService.stageXlsxFile(file, withExtendedSizes);

        sheetService.saveToS3(file, job.getId());

        return validatedSheet.fold(err -> {
            job.setStatus(StatusJobType.FAILED);
            jobRepo.save(job);
            val errorLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.ERROR).stagedJob(job)
                    .details(err.stream().map(ErrorInput::getMessage).collect(Collectors.joining(";"))).build();
            logRepo.save(errorLog);
            return ResponseEntity.unprocessableEntity()
                    .body(new UploadResponse(Optional.empty(), err.stream().toList()));
        }, parsedSheet -> {
            job.setStatus(StatusJobType.STAGED);
            jobRepo.save(job);

            List<StagedRow> rowsToSave = parsedSheet.getStagedRows();
            Long numEmptyRows = parsedSheet.getNumEmptyRows();

            val stagedLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.STAGED).stagedJob(job).details("Staged " + (rowsToSave.size())
                            + " row(s)." + (numEmptyRows > 0 ? " " + numEmptyRows + " empty " + "row(s) skipped." : ""))
                    .build();
            logRepo.save(stagedLog);
            stagedRowRepo.saveAll(rowsToSave.stream().map(s -> {
                s.setStagedJob(job);
                return s;
            }).collect(Collectors.toList()));
            val filesResult = new FileUpload(job.getId(), rowsToSave.size());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UploadResponse(Optional.of(filesResult), Collections.emptyList()));
        });
    }

    @PostMapping("/validate/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateJob(@PathVariable Long jobId, Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/validate",
                "validate job attempt for username " + authentication.getName() + " file: " + jobId));

        return jobRepo.findById(jobId).map(job -> {
            val validationResponse = validation.process(job);
            return ResponseEntity.ok().body(validationResponse);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ValidationResponse()));
    }

    @GetMapping("/job/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId) {
        val rows = stagedRowRepo.findRowsByJobId(jobId);
        return jobRepo.findById(jobId)
                .map(job -> ResponseEntity.ok().body(new JobResponse(job, rows, Collections.emptyList())))
                .orElseGet(() -> ResponseEntity.badRequest().body(new JobResponse(null, Collections.emptyList(),
                        Collections.singletonList(new ErrorInput("StagedJob Not found", "StagedJob")))));

    }

    @DeleteMapping("/delete/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Object> deleteJob(@PathVariable Long jobId) {
        jobRepo.deleteById(jobId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/job/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> updateJob(@PathVariable Long jobId, Authentication authentication,
            @RequestBody List<RowUpdateDto> rowUpdates) {

        List<Long> deletions = rowUpdates.stream().filter(u -> u.getRow() == null).map(u -> u.getRowId())
                .collect(Collectors.toList());
                
        List<StagedRow> updates = rowUpdates.stream().filter(u -> u.getRow() != null).map(u -> u.getRow())
                .collect(Collectors.toList());

        return rowService.save(jobId, deletions, updates).fold(
                err -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err),
                rowUpdate -> ResponseEntity.ok().body(rowUpdate));
    }
}
