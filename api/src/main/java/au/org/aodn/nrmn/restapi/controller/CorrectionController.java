package au.org.aodn.nrmn.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowPutDto;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.repository.CorrectionRowRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidationService;
import au.org.aodn.nrmn.restapi.service.validation.NullValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/correction")
@Tag(name = "correction")
public class CorrectionController {

    private static Logger logger = LoggerFactory.getLogger(CorrectionController.class);

    @Autowired
    private CorrectionRowRepository correctionRowRepository;

    @Autowired
    UserActionAuditRepository userAuditRepo;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    MeasurementValidationService measurementValidationService;

    @Autowired
    NullValidationService nullValidationService;
    
    @GetMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> getSurveyCorrection(@PathVariable("survey_id") Long surveyId) {
        var rows = correctionRowRepository.findRowsBySurveyId(surveyId);
        var exists = rows != null && rows.size() > 0;
        return exists ? ResponseEntity.ok(rows) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateSurveyCorrection(@PathVariable("survey_id") Long surveyId, Authentication authentication, @RequestBody List<CorrectionRowPutDto> rowUpdates) {
        
        String logMessage =  "correction validation: username: " + authentication.getName() + " survey: " + surveyId;
        userAuditRepo.save(new UserActionAudit("correct/survey", logMessage));

        List<Integer> observableItemIds = rowUpdates.stream().map(r -> r.getObservableItemId()).collect(Collectors.toList());
        var speciesAttributes = new HashMap<Integer, UiSpeciesAttributes>();
        observationRepository.getSpeciesAttributesByIds(observableItemIds).stream().forEach(m -> speciesAttributes.put(m.getId().intValue(), m));

        var observationIdsToReplace = new ArrayList<Integer>();
        Collection<ValidationError> errors = new HashSet<ValidationError>();
        for(var row: rowUpdates) {
            observationIdsToReplace.addAll(row.getObservationIds());
            errors.addAll(measurementValidationService.validate(speciesAttributes, row));
            errors.addAll(nullValidationService.validate(row));
        }

        logger.info("observations: " + observationIdsToReplace.toString());
        
        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyCorrection(@PathVariable("survey_id") Long surveyId, Authentication authentication, @RequestBody List<CorrectionRowPutDto> rowUpdates) {
        
        String logMessage =  "correction: username: " + authentication.getName() + " survey: " + surveyId;
        userAuditRepo.save(new UserActionAudit("correct/survey", logMessage));

        List<Integer> observableItemIds = rowUpdates.stream().map(r -> r.getObservableItemId()).collect(Collectors.toList());
        var speciesAttributes = new HashMap<Integer, UiSpeciesAttributes>();
        observationRepository.getSpeciesAttributesByIds(observableItemIds).stream().forEach(m -> speciesAttributes.put(m.getId().intValue(), m));

        var observationIdsToReplace = new ArrayList<Integer>();
        Collection<ValidationError> errors = new HashSet<ValidationError>();
        for(var row: rowUpdates) {
            observationIdsToReplace.addAll(row.getObservationIds());
            errors.addAll(measurementValidationService.validate(speciesAttributes, row));
            errors.addAll(nullValidationService.validate(row));
        }

        logger.info("observations: " + observationIdsToReplace.toString());
        
        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }
}