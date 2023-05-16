package au.org.aodn.nrmn.restapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.service.GlobalLockService;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.SurveyIngestionService;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/api/v1/ingestion")
@Tag(name = "Survey Ingestion")
public class IngestionController {

    protected static final Logger logger = LoggerFactory.getLogger(IngestionController.class);

    @Value("${nrmn.jobs.scheduleInitDelay:1000}")
    protected int scheduleInitDelay;

    @Autowired
    protected StagedJobRepository jobRepository;

    @Autowired
    protected StagedRowRepository rowRepository;

    @Autowired
    protected SurveyIngestionService surveyIngestionService;

    @Autowired
    protected StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    protected UserActionAuditRepository userActionAuditRepository;

    @Autowired
    protected SpeciesFormattingService speciesFormatting;

    @Autowired
    protected MaterializedViewService materializedViewService;

    @Autowired
    protected GlobalLockService globalLockService;

    protected ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostMapping(path = "ingest/{job_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Map> ingest(@PathVariable("job_id") Long jobId) {
        userActionAuditRepository.save(new UserActionAudit("ingestion/ingest", "ingest job: " + jobId));

        Map<String, Object> result = new HashMap<>();
        result.put("jobId", jobId);

        var optionalJob = jobRepository.findById(jobId);

        if (!optionalJob.isPresent()) {
            result.put("jobStatus", StatusJobType.FAILED);
            result.put("message", "Job with given id does not exist. jobId: " + jobId);
            logger.warn(result.get("message").toString());
            return ResponseEntity.badRequest().body(result);
        }

        final var job = optionalJob.get();
        if (job.getStatus() != StatusJobType.STAGED) {
            result.put("jobStatus", StatusJobType.FAILED);
            result.put("message", "Job with given id has not been validated: " + jobId);
            logger.warn(result.get("message").toString());
            return ResponseEntity.badRequest().body(result);
        }

        if (!globalLockService.setLock()) {
            result.put("jobStatus", StatusJobType.FAILED);
            result.put("reason", "locked");
            result.put("message", "Failed to set lock for job : " + jobId);
            logger.warn(result.get("message").toString());
            return ResponseEntity.badRequest().body(result);
        }

        final var stagedJobLog = stagedJobLogRepository
                .save(StagedJobLog.builder()
                        .stagedJob(job)
                        .eventType(StagedJobEventType.INGESTING)
                        .build());

        // Some upload is very large, use a separate to avoid timeout on web app
        executorService.schedule(() -> {

            try {

                logger.info("Ingest job id {} with transaction", job.getId());
                var rows = rowRepository.findRowsByJobId(job.getId());
                var species = speciesFormatting.getSpeciesForRows(rows);
                var validatedRows = speciesFormatting.formatRowsWithSpecies(rows, species);

                surveyIngestionService.ingestTransaction(job, validatedRows);

                logger.info("Refresh materialized view after job id {} ingested", job.getId());
                materializedViewService.refreshAllAsync();
            }
            catch (Exception e) {

                logger.error("Ingestion Failed", e);

                stagedJobLog.setDetails(e.getMessage());
                stagedJobLog.setEventType(StagedJobEventType.ERROR);

                stagedJobLogRepository.save(stagedJobLog);
            }
            finally {
                globalLockService.releaseLock();
            }
        }, scheduleInitDelay, TimeUnit.MILLISECONDS);

        result.put("jobStatus", stagedJobLog.getEventType());
        result.put("jobLogId", stagedJobLog.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "ingest/{job_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Map> getIngest(@PathVariable("job_id") Long jobId) {

        Map<String, Object> result = new HashMap<>();
        result.put("jobId", jobId);

        var optionalJob = stagedJobLogRepository.findById(jobId);

        if (!optionalJob.isPresent()) {
            result.put("jobStatus", StagedJobEventType.ERROR);
            result.put("message", "Job log does not exist. jobLogId: " + jobId);
        }
        else {
            result.put("jobStatus", optionalJob.get().getEventType());
        }
        return ResponseEntity.ok(result);
    }
}
