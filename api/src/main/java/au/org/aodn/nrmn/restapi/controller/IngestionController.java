package au.org.aodn.nrmn.restapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.db.model.StagedJobLog;
import au.org.aodn.nrmn.db.model.audit.UserActionAudit;
import au.org.aodn.nrmn.db.model.enums.StagedJobEventType;
import au.org.aodn.nrmn.db.model.enums.StatusJobType;
import au.org.aodn.nrmn.db.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.db.repository.StagedJobRepository;
import au.org.aodn.nrmn.db.repository.StagedRowRepository;
import au.org.aodn.nrmn.db.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.SurveyIngestionService;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/ingestion")
@Tag(name = "Survey Ingestion")
public class IngestionController {

    @Autowired
    StagedJobRepository jobRepository;

    @Autowired
    StagedRowRepository rowRepository;

    @Autowired
    SurveyIngestionService surveyIngestionService;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    UserActionAuditRepository userActionAuditRepository;

    @Autowired
    private ValidationProcess validation;

    @Autowired
    private MaterializedViewService materializedViewService;

    private static Logger logger = LoggerFactory.getLogger(IngestionController.class);

    @PostMapping(path = "ingest/{job_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<String> ingest(@PathVariable("job_id") Long jobId) {
        userActionAuditRepository.save(new UserActionAudit("ingestion/ingest", "ingest job: " + jobId));

        var optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent())
            return ResponseEntity.badRequest().body("Job with given id does not exist. jobId: " + jobId);

        var job = optionalJob.get();
        if (job.getStatus() != StatusJobType.STAGED)
            return ResponseEntity.badRequest().body("Job with given id has not been validated: " + jobId);

        try {

            var stagedJobLog = StagedJobLog.builder()
                    .stagedJob(job)
                    .eventType(StagedJobEventType.INGESTING)
                    .build();
            stagedJobLogRepository.save(stagedJobLog);

            var rows = rowRepository.findRowsByJobId(job.getId());
            var species = validation.getSpeciesForRows(rows);
            var validatedRows = validation.formatRowsWithSpecies(rows, species);
            
            surveyIngestionService.ingestTransaction(job, validatedRows);
            
            materializedViewService.refreshAllMaterializedViews();

        } catch (Exception e) {

            logger.error("Ingestion Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error ingesting sheet.")
                    .eventType(StagedJobEventType.ERROR).build();
            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Sheet failed to ingest. No survey data has been inserted.");
        }
        return ResponseEntity.ok("Job " + jobId + " successfully ingested.");
    }
}
