package au.org.aodn.nrmn.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowFormattedMapperConfig;
import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowMapperConfig;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.CorrectionRowRepository;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.service.SurveyCorrectionService;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidationService;
import au.org.aodn.nrmn.restapi.service.validation.ValidationConstraintService;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.ValidationResultSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/correction")
@Tag(name = "correction")
public class CorrectionController {

    @Autowired
    private CorrectionRowRepository correctionRowRepository;

    @Autowired
    UserActionAuditRepository userAuditRepo;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    StagedJobRepository jobRepository;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SecUserRepository userRepo;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    SurveyCorrectionService surveyCorrectionService;

    @Autowired
    MeasurementValidationService measurementValidationService;

    @Autowired
    ValidationConstraintService constraintService;

    private static Logger logger = LoggerFactory.getLogger(CorrectionController.class);

    private void logMessage(StagedJob job, String message) {
        var log = StagedJobLog.builder()
                .stagedJob(job).eventType(StagedJobEventType.CORRECTING)
                .details(message)
                .build();
        stagedJobLogRepository.save(log);
    }

    private List<StagedRowFormatted> formatRowsWithSpecies(Collection<StagedRow> rows,
            Collection<ObservableItem> species) {

        var rowMap = rows.stream().collect(Collectors.toMap(StagedRow::getId, r -> r));

        var speciesIds = species.stream()
                .mapToInt(s -> s.getObservableItemId())
                .toArray();

        var speciesAttributesMap = observationRepository
                .getSpeciesAttributesByIds(speciesIds).stream()
                .collect(Collectors.toMap(UiSpeciesAttributes::getSpeciesName, a -> a));

        var speciesMap = species.stream().collect(Collectors.toMap(ObservableItem::getObservableItemName, o -> o));

        var divers = diverRepository.getAll().stream().collect(Collectors.toList());

        var sites = siteRepository.getAll().stream().collect(Collectors.toList());

        var mapperConfig = new StagedRowFormattedMapperConfig();
        var mapper = mapperConfig.getModelMapper(speciesMap, rowMap, speciesAttributesMap, divers, sites);

        return rows.stream().map(stagedRow -> mapper.map(stagedRow, StagedRowFormatted.class))
                .collect(Collectors.toList());
    }

    private List<Pair<StagedRowFormatted, HashSet<String>>> mapRows(Collection<StagedRow> rows) {

        var speciesNames = rows.stream().map(s -> s.getSpecies()).collect(Collectors.toSet());
        var observableItems = observableItemRepository.getAllSpeciesNamesMatching(speciesNames);
        var formattedRows = formatRowsWithSpecies(rows, observableItems);

        var propertyChecks = new HashMap<String, Function<StagedRow, String>>() {
            {
                put("block", StagedRow::getBlock);
                put("date", StagedRow::getDate);
                put("depth", StagedRow::getDepth);
                put("direction", StagedRow::getDirection);
                put("diver", StagedRow::getDiver);
                put("latitude", StagedRow::getLatitude);
                put("longitude", StagedRow::getLongitude);
                put("method", StagedRow::getMethod);
                put("code", StagedRow::getCode);
                put("species", StagedRow::getSpecies);
                put("time", StagedRow::getTime);
                put("vis", StagedRow::getVis);
                put("snd", StagedRow::getSurveyNotDone);
                put("useInvertSizing", StagedRow::getIsInvertSizing);
            }
        };

        var result = new ArrayList<Pair<StagedRowFormatted, HashSet<String>>>();
        var modelMapper = StagedRowMapperConfig.GetModelMapper();
        for (var row : formattedRows) {

            var stagedRow = row.getRef();
            var mappedRow = modelMapper.map(row, StagedRow.class);

            var res = Pair.of(row, new HashSet<String>());
            var rowErrors = res.getRight();

            for (var entry : propertyChecks.entrySet())
                if (ObjectUtils.stringPropertiesDiffer(entry.getValue(), mappedRow, stagedRow))
                    rowErrors.add(entry.getKey());

            var mmA = stagedRow.getMeasureJson().entrySet().stream()
                    .filter(e -> !e.getValue().equals(mappedRow.getMeasureJson().get(e.getKey())))
                    .map(e -> e.getKey())
                    .collect(Collectors.toSet());

            // probably not necessary? 
            // (how can a measure exist in the mapped row that doesn't in the staged row?)
            var mmB = mappedRow.getMeasureJson().entrySet().stream()
                    .filter(e -> !e.getValue().equals(stagedRow.getMeasureJson().get(e.getKey())))
                    .map(e -> e.getKey())
                    .collect(Collectors.toSet());

            mmA.addAll(mmB);
            rowErrors.addAll(mmA.stream().map(e -> e.toString()).collect(Collectors.toSet()));

            result.add(res);
        }

        return result;
    }

    private ValidationResultSet mappingResultToValidation(List<Pair<StagedRowFormatted, HashSet<String>>> results) {
        var validationResult = new ValidationResultSet();

        var messages = new HashMap<String, String>() {
            {
                put("block", "Block is not an integer");
                put("date", "Date format not valid");
                put("depth", "Depth is not an interger");
                put("direction", "Direction is not valid");
                put("diver", "Diver does not exist");
                put("latitude", "Latitude is not a decimal");
                put("longitude", "Longitude is not a decimal");
                put("method", "Method is missing");
                put("code", "Site does not exist");
                put("species", "Species does not exist");
                put("time", "Time is not valid");
                put("vis", "Vis is not a number");
                put("snd", "Survey Not Done not valid");
                put("useInvertSizing", "Use Invert Sizing not valid");
                put("measurement", "Measurement is not valid");
            }
        };

        for (var result : results) {
            var row = result.getLeft();
            var rowErrors = result.getRight().stream()
                    .map(col -> new ValidationCell(
                            ValidationCategory.FORMAT,
                            ValidationLevel.BLOCKING,
                            messages.keySet().contains(col) ? messages.get(col) : messages.get("measurement"),
                            row.getId(),
                            col))
                    .collect(Collectors.toSet());
            validationResult.addAll(rowErrors, false);
        }

        return validationResult;
    }

    private ValidationResultSet validate(List<Pair<StagedRowFormatted, HashSet<String>>> results) {

        var validation = mappingResultToValidation(results);

        var mappedRows = results.stream().map(r -> r.getLeft()).collect(Collectors.toList());

        int[] obsItemIds = mappedRows.stream()
                .filter(r -> r.getSpecies().isPresent())
                .mapToInt(r -> r.getSpecies().get().getObservableItemId())
                .distinct()
                .toArray();

        var speciesAttributes = new HashMap<Integer, UiSpeciesAttributes>();
        observationRepository
                .getSpeciesAttributesByIds(obsItemIds)
                .stream()
                .forEach(m -> speciesAttributes.put(m.getId().intValue(), m));

        for (var row : mappedRows) {
            var attribute = row.getSpecies().isPresent()
                    ? speciesAttributes.get(row.getSpecies().get().getObservableItemId())
                    : null;
            if (attribute != null)
                validation.addAll(measurementValidationService.validate(attribute, row), false);
            // FUTURE: other validations go here ..
        }

        long errorId = 0;
        for (var error : validation.getAll())
            error.setId(errorId++);

        return validation;
    }

    @GetMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> getSurveyCorrection(@PathVariable("survey_id") Integer surveyId) {
        var rows = correctionRowRepository.findRowsBySurveyId(surveyId);
        var exists = rows != null && rows.size() > 0;
        return exists ? ResponseEntity.ok(rows) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateSurveyCorrection(
        @PathVariable("survey_id") Integer surveyId,
            Authentication authentication, 
            @RequestBody Collection<StagedRow> rows) {

        var message = "correction validation: username: " + authentication.getName() + " survey: " + surveyId;
        logger.debug("correction/validation", message);

        var response = new ValidationResponse();
        try {
            response.setErrors(validate(mapRows(rows)).getAll());
        } catch (Exception e) {
            logger.error("Validation Failed", e);
            return ResponseEntity.badRequest().body("Validation failed. Error: " + e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyCorrection(
            @PathVariable("survey_id") Integer surveyId,
            Authentication authentication,
            @RequestBody List<StagedRow> rows) {

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(surveyId);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        String logMessage = "correction: username: " + authentication.getName() + "survey: " + surveyId;
        userAuditRepo.save(new UserActionAudit("correct/survey", logMessage));

        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference(surveyId.toString())
                .status(StatusJobType.CORRECTION)
                .program(survey.getProgram())
                .creator(user.get())
                .build();

        job = jobRepository.save(job);

        logMessage(job, "Correct Survey " + surveyId);
        var response = new ValidationResponse();

        try {

            var results = mapRows(rows);
            var result = validate(results).getAll();
            var mappedRows = results.stream().map(r -> r.getLeft()).collect(Collectors.toList());
            var blockingErrors = result.stream().filter(r -> r.getLevelId() == ValidationLevel.BLOCKING).collect(Collectors.toList());

            if (blockingErrors.size() > 0) {
                logMessage(job, "Survey correction failed. Errors found.");
                return ResponseEntity.ok().body(result);
            }

            surveyCorrectionService.correctSurvey(job, survey, mappedRows);

        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error deleting survey " + survey.getSurveyId())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Survey failed to delete. No data has been changed.");
        }

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("correct/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyDeletion(
        @PathVariable Integer id, 
        Authentication authentication) {

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(id);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        userAuditRepo.save(new UserActionAudit("correction/delete", "survey: " + id));

        var job = jobRepository.save(StagedJob.builder().source(SourceJobType.CORRECTION)
                .reference(id.toString()).status(StatusJobType.CORRECTION)
                .program(survey.getProgram())
                .creator(user.get()).build());

        logMessage(job, "Delete Survey " + id);

        try {

            surveyCorrectionService.deleteSurvey(job, survey, Collections.emptyList());

        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error deleting survey " + survey.getSurveyId())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Survey failed to delete. No data has been changed.");
        }

        return ResponseEntity.ok("Survey " + id + " deleted.");
    }
}
