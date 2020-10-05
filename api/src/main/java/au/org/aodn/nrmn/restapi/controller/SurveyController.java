package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.service.StageSurveyService;
import au.org.aodn.nrmn.restapi.model.api.RawSurveyImport;
import au.org.aodn.nrmn.restapi.model.api.UpdatedResult;
import au.org.aodn.nrmn.restapi.model.api.ValidationResult;
import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.SurveyEntity;
import au.org.aodn.nrmn.restapi.repository.SurveyEntityRepository;
import au.org.aodn.nrmn.restapi.validation.ValidationProcess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/api")
public class SurveyController {

    @Autowired
    SurveyEntityRepository surveyRepo;

    @Autowired
    ValidationProcess validation;

    @Autowired
    StageSurveyService rawSurveyCRUD;

    private static Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(path = "/survey", produces = "application/json")
    public List<SurveyEntity> getSurvey( Authentication authentication) {
        logger.info("Survey reqested by:" + authentication.getName());
        return surveyRepo.findAll();
    }


    @GetMapping(path = "/raw-survey", produces = "application/json")
    public List<StagedJobEntity> getRawSurvey() {
        return rawSurveyCRUD.getSurveyFiles();
    }

    @GetMapping(path = "/raw-survey/{file_id}", produces = "application/json")
    public List<StagedSurveyEntity> getRawSurveyFile(@PathVariable("file_id") String file_id) {
        return rawSurveyCRUD.getRawSurveyFile(file_id);
    }

    @PostMapping(value = "/raw-survey", consumes = "application/json", produces = "application/json")
    public ValidationResult importRawSurvey(@RequestBody RawSurveyImport dataFile) {
        return validation.processList(dataFile.Rows, dataFile.fileID);
    }

    @PutMapping(value = "/raw-survey", consumes = "application/json", produces = "application/json")
    public UpdatedResult<StagedSurveyEntity, ErrorCheckEntity> updateRawSurvey(@RequestBody StagedSurveyEntity rawSurvey) {
        val entity = rawSurveyCRUD.update(rawSurvey);
        val res = validation.processError(rawSurvey);

        return new UpdatedResult<StagedSurveyEntity, ErrorCheckEntity>(entity, res.toList());
    }
}
