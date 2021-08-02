package au.org.aodn.nrmn.restapi.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.util.LogInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
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
import au.org.aodn.nrmn.restapi.service.S3IO;
import au.org.aodn.nrmn.restapi.service.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.StagedRowService;
import au.org.aodn.nrmn.restapi.service.SurveyContentsHandler.ParsedSheet;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/stage")
@Tag(name = "staged jobs")
public class StagedJobController {

    private static final Logger logger = LoggerFactory.getLogger(StagedJobController.class);

    private final String s3KeyTemplate = "raw-survey/jobid-%s.xlsx";

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
    
    @Autowired
    private S3IO s3client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @PostMapping("/upload")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("withExtendedSizes") Boolean withExtendedSizes,
            @RequestParam("programId") Integer programId, @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/upload", "upload excel file attempt for username: " + authentication.getName() + " file: " + file.getOriginalFilename()));

        Optional<Program> programOpt = programRepo.findById(programId);
        if (!programOpt.isPresent())
            return ResponseEntity.unprocessableEntity().body(new UploadResponse(Optional.empty(), "Program Not found"));

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        StagedJob job = StagedJob.builder().isExtendedSize(withExtendedSizes).source(SourceJobType.INGEST)
                .reference(file.getOriginalFilename()).status(StatusJobType.PENDING).program(programOpt.get())
                .creator(user.get()).build();
        jobRepo.save(job);

        StagedJobLog jobLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis()))
                .eventType(StagedJobEventType.UPLOADED).stagedJob(job)
                .details(file.getOriginalFilename() + " uploaded by:" + authentication.getName()).build();

        logRepo.save(jobLog);

        ResponseEntity<UploadResponse> responseEntity = null;

        try {
            ParsedSheet parsedSheet = sheetService.stageXlsxFile(file, withExtendedSizes);
            job.setStatus(StatusJobType.STAGED);
            jobRepo.save(job);

            List<StagedRow> rowsToSave = parsedSheet.getStagedRows();
            Long numEmptyRows = parsedSheet.getNumEmptyRows();

            String message = "Staged " + (rowsToSave.size()) + " row(s)." + (numEmptyRows > 0 ? " " + numEmptyRows + " empty " + "row(s) skipped." : "");
            StagedJobLog stagedLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis())).eventType(StagedJobEventType.STAGED).stagedJob(job).details(message).build();

            logRepo.save(stagedLog);

            String s3Key = String.format(s3KeyTemplate, job.getId());
            s3client.write(s3Key, file);

            message = String.format("Source file saved to \"%s/%s\"", bucketName, s3Key);
            stagedLog = StagedJobLog.builder()
                    .eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.STAGED)
                    .stagedJob(job)
                    .details(message).build();

            logRepo.save(stagedLog);

            rowsToSave.stream().forEach(s -> s.setStagedJob(job));
            stagedRowRepo.saveAll(rowsToSave);

            FileUpload filesResult = new FileUpload(job.getId(), rowsToSave.size());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(new UploadResponse(Optional.of(filesResult.getJobId()), null));

        } catch (Exception e) {

            String uploadError = e.getMessage();
            job.setStatus(StatusJobType.FAILED);
            jobRepo.save(job);

            StagedJobLog errorLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis())).eventType(StagedJobEventType.ERROR).stagedJob(job).details(uploadError).build();
            logRepo.save(errorLog);

            responseEntity = ResponseEntity.unprocessableEntity().body(new UploadResponse(Optional.empty(), uploadError));
        }

        return responseEntity;
    }

    @PostMapping("/validate/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateJob(@PathVariable Long jobId, Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/validate",
                "validate job attempt for username " + authentication.getName() + " file: " + jobId));

        return jobRepo.findById(jobId).map(job -> ResponseEntity.ok().body(validation.process(job)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ValidationResponse()));
    }

    @GetMapping("/job/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId) {
        List<StagedRow> rows = stagedRowRepo.findRowsByJobId(jobId);
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
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, Authentication authentication,
            @RequestBody List<RowUpdateDto> rowUpdates) {

        List<Long> deletions = rowUpdates.stream().filter(u -> u.getRow() == null).map(u -> u.getRowId())
                .collect(Collectors.toList());

        List<StagedRow> updates = rowUpdates.stream().filter(u -> u.getRow() != null).map(u -> u.getRow())
                .collect(Collectors.toList());

        Boolean success = rowService.save(jobId, deletions, updates);

        return success ? ResponseEntity.ok().body(null) : ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }

    @GetMapping("/job/download/{jobId}")
    public void downloadFile(final HttpServletResponse response, @PathVariable String jobId) {

        String s3Key = String.format(s3KeyTemplate, jobId);

        logger.info(LogInfo.withContext(
                String.format("Downloading original sheet for job %s from %s%s", jobId, bucketName, s3Key)));

        try {

            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(s3Key)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3client.getClient().getObjectAsBytes(objectRequest);
            response.getOutputStream().write(objectBytes.asByteArray());
            response.flushBuffer();

            logger.info(LogInfo.withContext(
                    String.format("Retrieved sheet for job %s from %s/%s", jobId, bucketName, s3Key)));

        } catch (IOException ioException) {
            logger.error(LogInfo.withContext(
                    String.format("Could not download sheet for job %s from \"%s/%s\".\n%s",
                            jobId, bucketName, s3Key, ioException.getMessage())));
        } catch (NoSuchKeyException keyException) {
            logger.error(LogInfo.withContext(String.format(
                    "key \"%s\" does not exist in bucket \"%s\". Could not download original file for job %s",
                    s3Key, bucketName, jobId)));
        } catch (SdkClientException clientException) {
            logger.error(LogInfo.withContext(String.format(
                    "Could not download original file for job %s. %s",
                    jobId, clientException.getMessage())));
        }
    }
}
