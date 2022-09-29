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

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.data.model.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.data.repository.CorrectionRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.data.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowFormattedMapperConfig;
import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowMapperConfig;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRequestBodyDto;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowsDto;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.SurveyCorrectionService;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.ValidationResultSet;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/correction")
@Tag(name = "correction")
public class CorrectionController {

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    MeasurementValidation measurementValidation;
    
    @Autowired
    DataValidation dataValidation;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    private CorrectionRowRepository correctionRowRepository;

    @Autowired
    private MaterializedViewService materializedViewService;

    @Autowired
    SecUserRepository secUserRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    StagedJobRepository stagedJobRepository;

    @Autowired
    SurveyCorrectionService surveyCorrectionService;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    UserActionAuditRepository userActionAuditRepository;

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

        // HACK: Survey Not Done
        if (speciesNames.stream().anyMatch(s -> s.equalsIgnoreCase("Survey Not Done"))) {
            var snd = new ObservableItem();
            snd.setObservableItemId(0);
            snd.setObservableItemName("Survey Not Done");
            observableItems.add(snd);
        }

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
                put("siteCode", StagedRow::getSiteCode);
                put("code", StagedRow::getCode);
                put("species", StagedRow::getSpecies);
                put("time", StagedRow::getTime);
                put("vis", StagedRow::getVis);
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
                put("siteCode", "Site does not exist");
                put("code", "Letter Code missing");
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

    private ValidationResultSet validate(ProgramValidation programValidation, Boolean isExtended,
            List<Pair<StagedRowFormatted, HashSet<String>>> results) {

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

            var speciesAttrib = row.getSpeciesAttributesOpt();
            if (speciesAttrib.isPresent())
                validation.addAll(measurementValidation.validate(speciesAttrib.get(), row, isExtended), false);

            // Total Checksum & Missing Data
            validation.addAll(measurementValidation.validateMeasurements(programValidation, row), false);

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
        var bodyDto = new CorrectionRowsDto();
        bodyDto.setRows(rows);
        var survey = surveyRepository.getReferenceById(surveyId);
        bodyDto.setProgramValidation(ProgramValidation.fromProgram(survey.getProgram()));
        return exists ? ResponseEntity.ok(bodyDto) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateSurveyCorrection(
            @PathVariable("survey_id") Integer surveyId,
            Authentication authentication,
            @RequestBody CorrectionRequestBodyDto bodyDto) {

        var message = "correction validation: username: " + authentication.getName() + " survey: " + surveyId;
        logger.debug("correction/validation", message);

        // does survey exist?
        if (surveyRepository.getReferenceById(surveyId) == null)
            return ResponseEntity.badRequest().body("Survey does not exist: " + surveyId);

        var response = new ValidationResponse();
        try {
            var errors = new ArrayList<SurveyValidationError>();
            var rows = bodyDto.getRows();
            errors.addAll(dataValidation.checkDuplicateRows(false, true, rows));
            errors.addAll(validate(bodyDto.getProgramValidation(),
                    bodyDto.getIsExtended(),
                    mapRows(rows))
                    .getAll());
            response.setErrors(errors);
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
            @RequestBody CorrectionRequestBodyDto bodyDto) {

        Optional<SecUser> user = secUserRepository.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(surveyId);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();
        var surveyName = String.format("[%s, %s, %s.%d]", survey.getSite().getSiteCode(), survey.getSurveyDate(),
                survey.getDepth(), survey.getSurveyNum());

        String logMessage = "correction: username: " + authentication.getName() + "survey: " + surveyId;
        userActionAuditRepository.save(new UserActionAudit("correct/survey", logMessage));

        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference("Correct Survey " + surveyName)
                .status(StatusJobType.CORRECTED)
                .program(survey.getProgram())
                .creator(user.get())
                .build();

        job = stagedJobRepository.save(job);

        logMessage(job, "Correct Survey " + surveyId);

        try {
            var results = mapRows(bodyDto.getRows());
            var result = validate(bodyDto.getProgramValidation(), bodyDto.getIsExtended(), results).getAll();
            var mappedRows = results.stream().map(r -> r.getLeft()).collect(Collectors.toList());
            var blockingErrors = result.stream().filter(r -> r.getLevelId() == ValidationLevel.BLOCKING)
                    .collect(Collectors.toList());

            if (blockingErrors.size() > 0) {
                logMessage(job, "Survey correction failed. Errors found.");
                return ResponseEntity.ok().body(result);
            }

            surveyCorrectionService.correctSurvey(job, survey, mappedRows);
            materializedViewService.refreshAllMaterializedViews();

        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error deleting survey " + survey.getSurveyId())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Survey failed to delete. No data has been changed.");
        }

        return ResponseEntity.ok().body(job.getId());
    }

    @DeleteMapping("correct/{id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyDeletion(
            @PathVariable Integer id,
            Authentication authentication) {

        var user = secUserRepository.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(id);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        if (survey.getPqCatalogued() != null && survey.getPqCatalogued())
            return ResponseEntity.badRequest().body("Deletion Failed. PQs catalogued for this survey.");

        userActionAuditRepository.save(new UserActionAudit("correction/delete", "survey: " + id));

        var job = stagedJobRepository.save(StagedJob.builder().source(SourceJobType.CORRECTION)
                .reference("Delete Survey " + id.toString()).status(StatusJobType.CORRECTED)
                .program(survey.getProgram())
                .creator(user.get()).build());

        logMessage(job, "Delete Survey " + id);

        try {
            surveyCorrectionService.deleteSurvey(job, survey, Collections.emptyList());
            materializedViewService.refreshAllMaterializedViews();
        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error deleting survey " + survey.getSurveyId())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Deletion failed. No data has been changed.");
        }

        return ResponseEntity.ok().body(job.getId());
    }
}
