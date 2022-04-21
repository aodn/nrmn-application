package au.org.aodn.nrmn.restapi.controller;

import java.util.Collection;
import java.util.Optional;

import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.SurveyIngestionService;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/ingestion")
@Tag(name = "ingestion")
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

    @PostMapping(path = "ingest/{job_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<String> ingest(@PathVariable("job_id") Long jobId) {
        userActionAuditRepository.save(new UserActionAudit("ingestion/ingest", "ingest job: " + jobId));

        Optional<StagedJob> optionalJob = jobRepository.findById(jobId);

        if (!optionalJob.isPresent()) {
            return ResponseEntity.badRequest().body("Job with given id does not exist. jobId: " + jobId);
        }

        StagedJob job = optionalJob.get();
        if (job.getStatus() != StatusJobType.STAGED) {
            return ResponseEntity.badRequest().body("Job with given id has not been validated: " + jobId);
        }

        try {
            stagedJobLogRepository
                    .save(StagedJobLog.builder().stagedJob(job).eventType(StagedJobEventType.INGESTING).build());

            Collection<StagedRow> rows = rowRepository.findRowsByJobId(job.getId());
            Collection<ObservableItem> species = validation.getSpeciesForRows(rows);
            Collection<StagedRowFormatted> validatedRows = validation.formatRowsWithSpecies(rows, species);
            surveyIngestionService.ingestTransaction(job, validatedRows);
            materializedViewService.refreshAllMaterializedViews();
        } catch (Exception e) {
            stagedJobLogRepository.save(StagedJobLog.builder().stagedJob(job).details(e.getMessage())
                    .eventType(StagedJobEventType.ERROR).build());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Job " + jobId + " successfully ingested.");
    }
}
