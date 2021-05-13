package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.SurveyIngestionService;
import au.org.aodn.nrmn.restapi.util.OptionalUtil;
import au.org.aodn.nrmn.restapi.validation.process.RawValidation;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/ingestion")
@Tag(name = "ingestion")
public class IngestionController {
    @Autowired
    RawValidation validation;
    @Autowired
    SurveyIngestionService surveyIngestionService;
    @Autowired
    StagedJobLogRepository stagedJobLogRepository;
    @Autowired
    StagedJobRepository jobRepository;
    @Autowired
    StagedRowRepository rowRepository;
    @Autowired
    UserActionAuditRepository userActionAuditRepository;

    @PostMapping(path = "ingest/{job_id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity ingest(@PathVariable("job_id") Long jobId) {
        userActionAuditRepository.save(
                new UserActionAudit(
                        "ingestion/ingest",
                        "ingest job: " + jobId));

        Optional<StagedJob> optionalJob = jobRepository.findById(jobId);
        if (!optionalJob.isPresent()) {
            return ResponseEntity.badRequest().body("Job with given id does not exist. jobId: " + jobId);
        }

        StagedJob job = optionalJob.get();
        if(job.getStatus() != StatusJobType.STAGED) {
            return ResponseEntity.badRequest().body("Job with given id has not been validated: " + jobId);
        }

        try {   
            stagedJobLogRepository.save(StagedJobLog.builder()
                    .stagedJob(job)
                    .eventType(StagedJobEventType.INGESTING)
                    .build());

            List<StagedRow> rows = rowRepository.findAll(Example.of(StagedRow.builder().stagedJob(job).build()));
            List<Integer> surveyIds =   validation.preValidated(rows, job).stream()
                    .flatMap(row -> {
                            val optSurvey= surveyIngestionService
                                    .ingestStagedRow(row)
                                    .stream().map(obs ->
                                        obs.getSurveyMethod().getSurvey())
                                    .findFirst();
                            return OptionalUtil.toStream(optSurvey);
                            })
                    .map(Survey::getSurveyId)
                    .distinct()
                    .collect(Collectors.toList());

            job.setStatus(StatusJobType.INGESTED);
            job.setSurveyIds(surveyIds
            );
            jobRepository.save(job);

            stagedJobLogRepository.save(StagedJobLog.builder()
                    .stagedJob(job)
                    .eventType(StagedJobEventType.INGESTED)
                    .build());
        } catch (Exception e) {
            stagedJobLogRepository.save(StagedJobLog.builder()
                    .stagedJob(job)
                    .details(e.getMessage())
                    .eventType(StagedJobEventType.ERROR)
                    .build());
            throw e;
        }

        return ResponseEntity.ok("job " + jobId + " successfully ingested.");
    }
}
