package au.org.aodn.nrmn.restapi.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
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

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.data.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.dto.site.RowUpdateDto;
import au.org.aodn.nrmn.restapi.dto.stage.FileUpload;
import au.org.aodn.nrmn.restapi.dto.stage.JobResponse;
import au.org.aodn.nrmn.restapi.dto.stage.StagedJobDto;
import au.org.aodn.nrmn.restapi.dto.stage.StagedJobRowDto;
import au.org.aodn.nrmn.restapi.dto.stage.UploadResponse;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.service.GlobalLockService;
import au.org.aodn.nrmn.restapi.service.StagedRowService;
import au.org.aodn.nrmn.restapi.service.upload.S3IO;
import au.org.aodn.nrmn.restapi.service.upload.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.upload.SurveyContentsHandler.ParsedSheet;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
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
@RequestMapping(path = "/api/v1/stage")
@Tag(name = "Staged Jobs")
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
    private DataValidation dataValidation;

    @Autowired
    private SecUserRepository userRepo;

    @Autowired
    private StagedJobLogRepository logRepo;

    @Autowired
    private GlobalLockService globalLockService;

    @Autowired
    private S3IO s3client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public List<StagedRow> getRowsToSave(ParsedSheet parsedSheet) {
        List<StagedRow> rowsToSave = parsedSheet.getStagedRows();

        // remove rows that are missing diver / site / date / depth
        rowsToSave.removeIf(r -> r.getDiver().isEmpty() && r.getSiteCode().isEmpty()
                && r.getDate().isEmpty()
                && r.getDepth().isEmpty());

        // remove duplicate rows from the end of the sheet
        StagedRow lastRow = rowsToSave.get(rowsToSave.size() - 1);

        if (lastRow.getTotal().equalsIgnoreCase("0")) {
            Long lastRowId = lastRow.getId();
            var rowsToTruncate = dataValidation.checkDuplicateRows(true, false, rowsToSave)
                    .stream()
                    .filter(v -> v.getRowIds().contains(lastRowId)).findAny();

            // only apply if there are three or more duplicate rows
            // (detect 3 duplicate rows rule)
            if (rowsToTruncate.isPresent() && rowsToTruncate.get().getRowIds().size() >= 3) {
                Collection<Long> rowIdsToRemove = rowsToTruncate.get().getRowIds();

                // if a row is not the duplicated one in the bottom of the sheet, don't remove it
                var bottomDuplicateRowIds = filterBottomDuplicateRows(rowIdsToRemove, lastRowId);

                // keep the last row if the data should finish with a true zero
                if (Arrays.asList("SURVEY NOT DONE", "DEBRIS - ZERO", "NO SPECIES FOUND")
                        .contains(lastRow.getSpecies().toUpperCase()))
                    bottomDuplicateRowIds.remove(lastRowId);
                rowsToSave.removeIf(r -> bottomDuplicateRowIds.contains(r.getId()));
            }
        }
        return rowsToSave;
    }



    @PostMapping("/upload")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("withExtendedSizes") Boolean withExtendedSizes,
                                                     @RequestParam("programId") Integer programId, @RequestParam("file") MultipartFile file,
                                                     Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/upload", "upload excel file attempt for username: "
                + authentication.getName() + " file: " + file.getOriginalFilename()));

        Optional<Program> programOpt = programRepo.findById(programId);
        if (!programOpt.isPresent())
            return ResponseEntity.unprocessableEntity()
                    .body(new UploadResponse(Optional.empty(), "Program Not found"));

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        StagedJob job = StagedJob.builder().isExtendedSize(withExtendedSizes).source(SourceJobType.INGEST)
                .reference(file.getOriginalFilename()).status(StatusJobType.PENDING)
                .program(programOpt.get())
                .creator(user.get()).build();

        logger.info("JobRepo save job {}", file.getOriginalFilename());
        jobRepo.save(job);

        StagedJobLog jobLog = StagedJobLog.builder().eventTime(new Timestamp(System.currentTimeMillis()))
                .eventType(StagedJobEventType.UPLOADED).stagedJob(job)
                .details(file.getOriginalFilename() + " uploaded by:" + authentication.getName())
                .build();

        logger.info("JobLog save job {}", file.getOriginalFilename());
        logRepo.save(jobLog);

        ResponseEntity<UploadResponse> responseEntity = null;

        try {
            ParsedSheet parsedSheet = sheetService.stageXlsxFile(file, withExtendedSizes);
            int totalRows = parsedSheet.getStagedRows().size();
            job.setStatus(StatusJobType.STAGED);

            logger.info("JobRepo save job after file {} parsed", file.getOriginalFilename());
            jobRepo.save(job);

            List<StagedRow> rowsToSave = getRowsToSave(parsedSheet);

            int invalidRows = totalRows - rowsToSave.size();
            String message = "Saved " + (rowsToSave.size()) + " rows. Removed " + invalidRows + " invalid rows.";
            StagedJobLog stagedLog = StagedJobLog.builder()
                    .eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.STAGED).stagedJob(job)
                    .details(message).build();

            logger.info("JobLog save job after file {} parsed", file.getOriginalFilename());
            logRepo.save(stagedLog);

            String s3Key = String.format(s3KeyTemplate, job.getId());
            s3client.write(s3Key, file);

            logRepo.save(StagedJobLog.builder()
                    .eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.STAGED)
                    .stagedJob(job)
                    .details(String.format("Source file saved to \"%s/%s\"", bucketName, s3Key))
                    .build());

            rowsToSave.stream().forEach(s -> {
                s.setStagedJob(job);
                s.setId(null);
            });
            stagedRowRepo.saveAll(rowsToSave);

            FileUpload filesResult = new FileUpload(job.getId(), rowsToSave.size());
            responseEntity = ResponseEntity.status(HttpStatus.OK)
                    .body(new UploadResponse(Optional.of(filesResult.getJobId()), null, message));

        } catch (Exception e) {

            String uploadError = e.getMessage();
            job.setStatus(StatusJobType.FAILED);
            jobRepo.save(job);

            StagedJobLog errorLog = StagedJobLog.builder()
                    .eventTime(new Timestamp(System.currentTimeMillis()))
                    .eventType(StagedJobEventType.ERROR).stagedJob(job).details(uploadError)
                    .build();
            logRepo.save(errorLog);

            responseEntity = ResponseEntity.unprocessableEntity()
                    .body(new UploadResponse(Optional.empty(), uploadError));
        }

        return responseEntity;
    }

    @PostMapping("/validate/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateJob(@PathVariable Long jobId, Authentication authentication) {

        userAuditRepo.save(new UserActionAudit("stage/validate",
                "validate job attempt for username " + authentication.getName() + " file: " + jobId));

        if (globalLockService.setLock()) {
            try {
                return jobRepo.findById(jobId)
                        .map(job -> ResponseEntity.ok(validation.process(job)))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ValidationResponse()));
            } finally {
                globalLockService.releaseLock();
            }
        } else {
            return ResponseEntity.ok("locked");
        }

    }

    @GetMapping("/jobs")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<List<StagedJobRowDto>> getJobs() {
        List<StagedJobRowDto> rows = jobRepo.findAll().stream()
                .map(j -> StagedJobRowDto.builder().id(j.getId())
                        .reference(j.getReference())
                        .program(j.getProgram().getProgramName())
                        .source(j.getSource().toString())
                        .isExtendedSize(j.getIsExtendedSize())
                        .status(j.getStatus().toString())
                        .creator(j.getCreator().getEmail().toString())
                        .created(j.getCreated()).build())
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(rows);
    }

    @GetMapping("/stagedJob/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<StagedJobDto> getStagedJob(@PathVariable Long jobId) {
        var jOptional = jobRepo.findById(jobId);
        if (!jOptional.isPresent())
            return ResponseEntity.notFound().build();

        var j = jOptional.get();
        var dto = StagedJobDto.builder()
                .id(j.getId())
                .isExtendedSize(j.getIsExtendedSize())
                .programName(j.getProgram().getProgramName())
                .reference(j.getReference())
                .status(j.getStatus())
                .source(j.getSource())
                .creatorEmail(j.getCreator().getEmail())
                .created(j.getCreated())
                .surveyIds(j.getSurveyIds()).logs(j.getLogs()).build();
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping("/job/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId) {
        var rows = stagedRowRepo.findRowsByJobId(jobId);
        return jobRepo.findById(jobId)
                .map(job -> ResponseEntity.ok(new JobResponse(job, rows)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/delete/{jobId}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Object> deleteJob(@PathVariable Long jobId) {
        var jOptional = jobRepo.findById(jobId);
        if (!jOptional.isPresent())
            return ResponseEntity.notFound().build();
        var job = jOptional.get();
        if (job.getStatus() != StatusJobType.INGESTED) {
            jobRepo.deleteById(jobId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
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

        return success ? ResponseEntity.ok().body(null)
                : ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
    }

    @GetMapping("/job/download/{jobId}")
    public void downloadFile(final HttpServletResponse response, @PathVariable String jobId) {

        String s3Key = String.format(s3KeyTemplate, jobId);

        logger.info(LogInfo.withContext(
                String.format("Downloading original sheet for job %s from %s%s", jobId, bucketName,
                        s3Key)));

        try {

            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(s3Key)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3client.getClient()
                    .getObjectAsBytes(objectRequest);
            response.getOutputStream().write(objectBytes.asByteArray());
            response.flushBuffer();

            logger.info(LogInfo.withContext(
                    String.format("Retrieved sheet for job %s from %s/%s", jobId, bucketName,
                            s3Key)));

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

    /**
     * This method is used to get the bottom duplicate rows.
     * E.g. If the collection of rowIdsToRemove is {1, 3, 6, 8, 9, 10, 11}, this method will return {8, 9, 10, 11}
     * @param rowIdsToRemove
     * @param lastRowId
     * @return bottom duplicate rows
     */
    private Collection<Long> filterBottomDuplicateRows(Collection<Long> rowIdsToRemove, Long lastRowId) {

        var bottomDuplicateRowIds = new ArrayList<Long>();

        // This for loop here is checking whether there are duplicate rows at the end. start from the bottom of the sheet.
        for (var i = 0; i < rowIdsToRemove.size(); i++) {

            // The id will be multiplied by 1000 somewhere. So we need to multiply the index by 1000 to get the correct id.
            var checked = lastRowId - i * 1000L;
            if (!rowIdsToRemove.contains(checked)) break;
            bottomDuplicateRowIds.add(checked);
        }

        return bottomDuplicateRowIds;
    }
}
