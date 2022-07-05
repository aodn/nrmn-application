package au.org.aodn.nrmn.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
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
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.model.db.Site;
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
    StagedJobRepository jobRepository;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SecUserRepository userRepo;

    @Autowired
    SurveyCorrectionService surveyCorrectionService;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    private void logMessage(StagedJob job, String message) {
        var log = StagedJobLog.builder().stagedJob(job).eventType(StagedJobEventType.CORRECTING).details(message)
                .build();
        stagedJobLogRepository.save(log);
    }

    public StagedRowFormatted formatRow(List<ObservableItem> species, StagedRow row) {
        StagedRowFormatted formatted = new StagedRowFormatted();

        formatted.setIsInvertSizing(Boolean.parseBoolean(row.getIsInvertSizing()));
        formatted.setSpecies(
                species.stream().filter(s -> s.getObservableItemName().equalsIgnoreCase(row.getSpecies())).findFirst());
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
    public ResponseEntity<?> getSurveyCorrection(@PathVariable("survey_id") Integer surveyId) {
        var rows = correctionRowRepository.findRowsBySurveyId(surveyId);
        var exists = rows != null && rows.size() > 0;
        return exists ? ResponseEntity.ok(rows) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> validateSurveyCorrection(@PathVariable("survey_id") Integer surveyId,
            Authentication authentication, @RequestBody Collection<StagedRow> rowUpdates) {

        var message = "correction validation: username: " + authentication.getName() + " survey: " + surveyId;
        logger.debug("correction/validation", message);
        Collection<ValidationError> errors = new HashSet<ValidationError>();

        try {
            var speciesNames = rowUpdates.stream().map(r -> r.getSpecies()).collect(Collectors.toSet());
            var species = observableItemRepository.getAllSpeciesNamesMatching(speciesNames);

            var speciesAttributesMap = new HashMap<Integer, UiSpeciesAttributes>();
            observationRepository.getSpeciesAttributesBySpeciesNames(speciesNames).stream()
                    .forEach(m -> speciesAttributesMap.put(m.getId().intValue(), m));
            var existingSurveyOptional = surveyRepository.findById(surveyId);
            if (!existingSurveyOptional.isPresent())
                return ResponseEntity.notFound().build();

            var existingSurvey = existingSurveyOptional.get();
            var observationIdsToReplace = new ArrayList<Integer>();
            errors.addAll(constraintService.validate(existingSurvey, rowUpdates));
            for (var row : rowUpdates) {
                observationIdsToReplace.addAll(row.getObservationIds());
                var formattedRow = formatRow(species, row);
                errors.addAll(measurementValidationService.validate(
                        speciesAttributesMap.get(formattedRow.getSpecies().get().getObservableItemId()), formattedRow));
            }

            logger.info("observations: " + observationIdsToReplace.toString());
        } catch (Exception exception) {
            errors.add(new ValidationError(ValidationCategory.RUNTIME, ValidationLevel.BLOCKING, exception.getMessage(),
                    null, null));
        }
        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }

    public Collection<StagedRowFormatted> formatRowsWithSpecies(Collection<StagedRow> rows,
            Collection<ObservableItem> species) {
        Map<Long, StagedRow> rowMap = rows.stream().collect(Collectors.toMap(StagedRow::getId, r -> r));
        List<Integer> speciesIds = species.stream().map(s -> s.getObservableItemId()).collect(Collectors.toList());
        Map<String, UiSpeciesAttributes> speciesAttributesMap = observationRepository
                .getSpeciesAttributesByIds(speciesIds).stream()
                .collect(Collectors.toMap(UiSpeciesAttributes::getSpeciesName, a -> a));
        Map<String, ObservableItem> speciesMap = species.stream()
                .collect(Collectors.toMap(ObservableItem::getObservableItemName, o -> o));
        Collection<Diver> divers = diverRepository.getAll().stream().collect(Collectors.toList());
        Collection<Site> sites = siteRepository.getAll().stream().collect(Collectors.toList());

        StagedRowFormattedMapperConfig mapperConfig = new StagedRowFormattedMapperConfig();
        ModelMapper mapper = mapperConfig.getModelMapper(speciesMap, rowMap, speciesAttributesMap, divers, sites);
        return rows.stream().map(stagedRow -> mapper.map(stagedRow, StagedRowFormatted.class))
                .collect(Collectors.toList());
    }

    private boolean properiesDiffer(Function<StagedRow, String> getter, StagedRow rowA, StagedRow rowB) {
        var valueA = getter.apply(rowA);
        var valueB = getter.apply(rowB);
        return StringUtils.isNotEmpty(valueA) &&
                StringUtils.isNotEmpty(valueB) &&
                !valueA.equalsIgnoreCase(valueB);
    }

    private List<Pair<StagedRowFormatted, HashSet<String>>> mapRows(
            Collection<ObservableItem> species,
            Collection<StagedRow> rows) {

        var mappedRows = formatRowsWithSpecies(rows, species);

        var result = new ArrayList<Pair<StagedRowFormatted, HashSet<String>>>();

        // -- model mapping

        var modelMapper = new ModelMapper();

        var obsItemMapper = (Converter<Optional<ObservableItem>, String>) ctx -> {
            return ctx.getSource().isPresent() ? ctx.getSource().get().getObservableItemName() : null;
        };

        modelMapper.typeMap(StagedRowFormatted.class, StagedRow.class)
                .addMappings(mapper -> mapper.map(src -> src.getDiver().getInitials(), StagedRow::setDiver))
                .addMappings(mapper -> mapper.using(obsItemMapper)
                        .map(StagedRowFormatted::getSpecies, StagedRow::setSpecies));

        // ---

        for (var row : mappedRows) {

            var res = Pair.of(row, new HashSet<String>());

            var demappedRow = modelMapper.map(row, StagedRow.class);

            var stagedRow = row.getRef();

            var rowErrors = res.getRight();

            if (properiesDiffer(StagedRow::getDiver, demappedRow, stagedRow))
                rowErrors.add("diver");

            if (properiesDiffer(StagedRow::getDepth, demappedRow, stagedRow))
                rowErrors.add("depth");

            if (properiesDiffer(StagedRow::getDate, demappedRow, stagedRow))
                rowErrors.add("date");

            if (properiesDiffer(StagedRow::getTime, demappedRow, stagedRow))
                rowErrors.add("time");

            if (properiesDiffer(StagedRow::getVis, demappedRow, stagedRow))
                rowErrors.add("vis");

            if (properiesDiffer(StagedRow::getDirection, demappedRow, stagedRow))
                rowErrors.add("direction");

            if (properiesDiffer(StagedRow::getLatitude, demappedRow, stagedRow))
                rowErrors.add("latitude");

            if (properiesDiffer(StagedRow::getLongitude, demappedRow, stagedRow))
                rowErrors.add("longitude");

            if (properiesDiffer(StagedRow::getSpecies, demappedRow, stagedRow))
                rowErrors.add("species");

            if (properiesDiffer(StagedRow::getCode, demappedRow, stagedRow))
                rowErrors.add("code");

            if (properiesDiffer(StagedRow::getMethod, demappedRow, stagedRow))
                rowErrors.add("method");

            if (properiesDiffer(StagedRow::getBlock, demappedRow, stagedRow))
                rowErrors.add("block");

            if (properiesDiffer(StagedRow::getSurveyNotDone, demappedRow, stagedRow))
                rowErrors.add("snd");

            if (properiesDiffer(StagedRow::getIsInvertSizing, demappedRow, stagedRow))
                rowErrors.add("useInvertSizing");

            // if
            // (!demappedRow.getMeasureJson().equalsIgnoreCase(stagedRow.getMeasureJson()))
            // rowErrors.add("measures");

            result.add(res);
        }

        return result;
    }

    @PostMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyCorrection(@PathVariable("survey_id") Integer surveyId,
            Authentication authentication, @RequestBody List<StagedRow> rows) {

        // -- preamble

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(surveyId);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        String logMessage = "correction: username: " + authentication.getName() + "survey: " + surveyId;
        userAuditRepo.save(new UserActionAudit("correct/survey", logMessage));

        // create a new job for this correction

        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference(surveyId.toString())
                .status(StatusJobType.CORRECTION)
                .program(survey.getProgram())
                .creator(user.get())
                .build();

        job = jobRepository.save(job);

        logMessage(job, "Correct Survey " + surveyId);

        // ---

        var speciesNames = rows.stream().map(s -> s.getSpecies()).collect(Collectors.toSet());
        var observableItems = observableItemRepository.getAllSpeciesNamesMatching(speciesNames);

        var speciesAttributes = new HashMap<Integer, UiSpeciesAttributes>();
        var obsItemIds = observableItems.stream().map(o -> o.getObservableItemId()).collect(Collectors.toList());
        observationRepository
                .getSpeciesAttributesByIds(obsItemIds)
                .stream()
                .forEach(m -> speciesAttributes.put(m.getId().intValue(), m));

        var mappingResult = mapRows(observableItems, rows);

        var mappedRows = mappingResult.stream().map(r -> r.getLeft()).collect(Collectors.toList());

        var errors =  new HashSet<ValidationError>();
        for (var row : mappedRows) {
            var speciesAttrib = speciesAttributes.get(row.getSpecies().get().getObservableItemId());
            errors.addAll(measurementValidationService.validate(speciesAttrib, row));
        }

        if (errors.size() < 1)
            surveyCorrectionService.correctSurvey(job, survey, mappedRows);
        else
            logMessage(job, "Survey correction failed. Errors found.");

        var response = new ValidationResponse();
        response.setErrors(errors);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("correct/{id}")
    public ResponseEntity<?> submitSurveyDeletion(@PathVariable Integer id, Authentication authentication) {

        Optional<SecUser> user = userRepo.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(id);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        userAuditRepo.save(new UserActionAudit("correction/delete", "survey: " + id));

        var job = jobRepository.save(StagedJob.builder().source(SourceJobType.CORRECTION)
                .reference(String.format("%d", id)).status(StatusJobType.CORRECTION)
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
