package au.org.aodn.nrmn.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
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
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.CorrectionRowRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidationService;
import au.org.aodn.nrmn.restapi.service.validation.ValidationConstraintService;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
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
    ObservableItemRepository observableItemRepository;

    @Autowired
    MeasurementValidationService measurementValidationService;

    @Autowired
    ValidationConstraintService constraintService;

    @Autowired
    SurveyRepository surveyRepository;

    public StagedRowFormatted formatRow(List<ObservableItem> species, StagedRow row) {
        StagedRowFormatted formatted = new StagedRowFormatted();

        formatted.setIsInvertSizing(Boolean.parseBoolean(row.getIsInvertSizing()));
        formatted.setSpecies(species.stream().filter(s -> s.getObservableItemName().equalsIgnoreCase(row.getSpecies())).findFirst());
        formatted.setRef(row);

        Map<Integer, Integer> measures = new HashMap<Integer, Integer>();
        if (row.getMeasureJson() != null) {
            for (Map.Entry<Integer, String> entry : row.getMeasureJson().entrySet()) {
                int val = NumberUtils.toInt(entry.getValue(), Integer.MIN_VALUE);
                if (val != Integer.MIN_VALUE)
                    measures.put(entry.getKey(), val);
            }
        }
        formatted.setMeasureJson(measures);
        return formatted;
    }
    
    @GetMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> getSurveyCorrection(@PathVariable("survey_id") Long surveyId) {
        var rows = correctionRowRepository.findRowsBySurveyId(surveyId);
        var exists = rows != null && rows.size() > 0;
        return exists ? ResponseEntity.ok(rows) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateSurveyCorrection(@PathVariable("survey_id") Integer surveyId, Authentication authentication, @RequestBody Collection<StagedRow> rowUpdates) {
        
        var message =  "correction validation: username: " + authentication.getName() + " survey: " + surveyId;
        logger.debug("correction/validation", message);
        Collection<ValidationError> errors = new HashSet<ValidationError>();

        try
        {
            var speciesNames = rowUpdates.stream().map(r -> r.getSpecies()).collect(Collectors.toSet());
            var species = observableItemRepository.getAllSpeciesNamesMatching(speciesNames);

            var speciesAttributesMap = new HashMap<Integer, UiSpeciesAttributes>();
            observationRepository.getSpeciesAttributesBySpeciesNames(speciesNames).stream().forEach(m -> speciesAttributesMap.put(m.getId().intValue(), m));
            var existingSurveyOptional = surveyRepository.findById(surveyId);
            if(!existingSurveyOptional.isPresent())
                return ResponseEntity.notFound().build();
                
            var existingSurvey = existingSurveyOptional.get();
            var observationIdsToReplace = new ArrayList<Integer>();
            errors.addAll(constraintService.validate(existingSurvey, rowUpdates));
            for(var row: rowUpdates) {
                observationIdsToReplace.addAll(row.getObservationIds());
                var formattedRow = formatRow(species, row);
                errors.addAll(measurementValidationService.validate(speciesAttributesMap.get(formattedRow.getSpecies().get().getObservableItemId()), formattedRow));
            }

            logger.info("observations: " + observationIdsToReplace.toString());
        }
        catch(Exception exception)
        {
            errors.add(new ValidationError(ValidationCategory.RUNTIME, ValidationLevel.BLOCKING, exception.getMessage(), null, null));
        }
        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyCorrection(@PathVariable("survey_id") Long surveyId, Authentication authentication, @RequestBody List<StagedRow> rows) {
        
        // String logMessage =  "correction: username: " + authentication.getName() + " survey: " + surveyId;
        // userAuditRepo.save(new UserActionAudit("correct/survey", logMessage));

        // List<Integer> observableItemIds = rows.stream().map(r -> r.getObservableItemId()).collect(Collectors.toList());
        // var speciesAttributes = new HashMap<Integer, UiSpeciesAttributes>();
        // observationRepository.getSpeciesAttributesByIds(observableItemIds).stream().forEach(m -> speciesAttributes.put(m.getId().intValue(), m));

        // var observationIdsToReplace = new ArrayList<Integer>();
        Collection<ValidationError> errors = new HashSet<ValidationError>();
        // for(var row: rows) {
        //     observationIdsToReplace.addAll(row.getObservationIds());
        //     errors.addAll(measurementValidationService.validate(speciesAttributes, row));
        // }

        // logger.info("observations: " + observationIdsToReplace.toString());
        
        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }
}