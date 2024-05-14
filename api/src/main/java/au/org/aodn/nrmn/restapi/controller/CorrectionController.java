package au.org.aodn.nrmn.restapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.util.SpacialUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.Precision;
import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowMapperConfig;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedJobLog;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.data.model.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.data.repository.CorrectionRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.data.repository.SecUserRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobLogRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.data.repository.UserActionAuditRepository;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRequestBodyDto;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowsDto;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesCorrectBodyDto;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesCorrectResultDto;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesSearchBodyDto;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.SurveyCorrectionService;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;
import au.org.aodn.nrmn.restapi.service.validation.ValidationResultSet;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import static au.org.aodn.nrmn.restapi.util.Constants.COORDINATE_VALID_DECIMAL_COUNT;
import static au.org.aodn.nrmn.restapi.util.Constants.SURVEY_LOCATION_TOLERANCE;

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
    SiteValidation siteValidation;

    @Autowired
    StagedJobLogRepository stagedJobLogRepository;

    @Autowired
    StagedJobRepository stagedJobRepository;

    @Autowired
    SurveyCorrectionService surveyCorrectionService;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SurveyValidation surveyValidation;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    UserActionAuditRepository userActionAuditRepository;

    @Autowired
    SpeciesFormattingService speciesFormatting;

    @Autowired
    ObjectMapper objectMapper;

    private static Logger logger = LoggerFactory.getLogger(CorrectionController.class);

    private void logMessage(StagedJob job, String message) {
        var log = StagedJobLog.builder()
                .stagedJob(job).eventType(StagedJobEventType.CORRECTING)
                .details(message)
                .build();
        stagedJobLogRepository.save(log);
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

        var formattedRows = speciesFormatting.formatRowsWithSpecies(rows, observableItems);

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

    private ValidationResultSet validate(
            ProgramValidation programValidation,
            List<Integer> surveyIds,
            List<Pair<StagedRowFormatted, HashSet<String>>> results) {

        var validation = new ValidationResultSet();

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
                validation.addAll(measurementValidation.validate(speciesAttrib.get(), row, true), false);

            // Total Checksum & Missing Data
            validation.addAll(measurementValidation.validateMeasurements(programValidation, row), false);

            // Row Method is valid for species
            var allowM11 = programValidation == ProgramValidation.ATRC;
            validation.add(surveyValidation.validateSpeciesBelowToMethod(allowM11, row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            validation.add(surveyValidation.validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            validation.add(surveyValidation.validateDateRange(programValidation, row), false);

            // Site distance validation
            if (programValidation != ProgramValidation.NONE)
                validation.add(siteValidation.validateSurveyAtSite(row));
        }

        surveyValidation.validateSurveysMatch(surveyIds, mappedRows).forEach(surveyMatchError -> {
            validation.add(null, ValidationLevel.BLOCKING, "ID", surveyMatchError);
        });

        validation.addAll(siteValidation.validateSites(mappedRows));

        validation.addAll(surveyValidation.validateSurveys(programValidation, true, mappedRows));

        validation.addAll(surveyValidation.validateSurveyGroups(programValidation, true, mappedRows));

        long errorId = 0;
        for (var error : validation.getAll())
            error.setId(errorId++);

        return validation;
    }

    @GetMapping(path = "correct")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> getSurveyCorrections(@RequestParam("surveyIds") List<Integer> surveyIds) {

        var lockedSurveys = surveyRepository.findAllById(surveyIds).stream()
                .filter(s -> s.getLocked() != null && s.getLocked())
                .collect(Collectors.toList());

        if (lockedSurveys.size() > 0) {
            var locked = lockedSurveys.stream().map(s -> s.getSurveyId().toString()).collect(Collectors.joining(", "));
            // The return type is application/json
            return ResponseEntity.badRequest()
                    .body(String.format("{ \"message\": \"Surveys are locked and cannot be corrected: %s\" }", locked));
        }

        var programs = correctionRowRepository.findProgramsBySurveyIds(surveyIds)
                .stream()
                .collect(Collectors.toList());

        var programValidations = programs.stream().map(ProgramValidation::fromProgram)
                .collect(Collectors.toList());

        if (programValidations.size() != 1)
            return ResponseEntity.badRequest()
                    .body("{ \"message\": \"Surveys must share the same program validation\" }");
        var program = programs.get(0);

        var rows = correctionRowRepository.findRowsBySurveyIds(surveyIds);
        var exists = rows != null && rows.size() > 0;
        var bodyDto = new CorrectionRowsDto();
        bodyDto.setRows(rows);
        bodyDto.setProgramName(program.getProgramName());
        bodyDto.setProgramId(program.getProgramId());
        bodyDto.setSurveyIds(surveyIds);

        return exists ? ResponseEntity.ok(bodyDto) : ResponseEntity.notFound().build();
    }

    @PostMapping(path = "validate")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> validateSurveyCorrection(
            Authentication authentication,
            @RequestParam("surveyIds") List<Integer> surveyIds,
            @RequestBody CorrectionRequestBodyDto bodyDto) {

        var message = "correction validation: username: " + authentication.getName();
        logger.debug("correction/validation", message);

        var response = new ValidationResponse();

        try {
            var errors = new ArrayList<SurveyValidationError>();
            var rows = bodyDto.getRows();


            var mappedRows = mapRows(rows);

            var siteCodes = mappedRows.stream()
                    .filter(r -> r.getKey().getSite() != null)
                    .map(r -> r.getKey().getSite().getSiteCode().toLowerCase())
                    .distinct().collect(Collectors.toList());

            var observableItems = mappedRows.stream()
                    .filter(r -> r.getKey().getSpecies().isPresent())
                    .map(r -> r.getKey().getSpecies().get())
                    .collect(Collectors.toList());

            var programValidation = ProgramValidation
                    .fromProgram(programRepository.findById(bodyDto.getProgramId()).get());

            errors.addAll(dataValidation.checkFormatting(programValidation, true, false,
                    siteCodes, observableItems, rows));

            errors.addAll(validate(programValidation, surveyIds, mappedRows).getAll());

            response.setErrors(errors);

            Collection<StagedRow> validatedRows = validateLatLonInDiff(rows, errors);

            var summary = surveyCorrectionService.diffSurveyCorrections(surveyIds, validatedRows);
            response.setSummary(summary);

        } catch (Exception e) {
            logger.error("Validation Failed", e);
            return ResponseEntity.badRequest().body("Validation failed. Error: " + e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "correct")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> submitSurveyCorrection(
            @RequestParam("surveyIds") List<Integer> surveyIds,
            Authentication authentication,
            @RequestBody CorrectionRequestBodyDto bodyDto) {

        var user = secUserRepository.findByEmail(authentication.getName());
        var program = programRepository.findById(bodyDto.getProgramId()).get();
        var programValidation = ProgramValidation.fromProgram(program);
        var survey = surveyIds.stream().map(Object::toString).collect(Collectors.joining(", "));

        var message = "correction: username: " + authentication.getName();
        userActionAuditRepository.save(new UserActionAudit("correct/survey", message));

        var surveyCount = surveyIds.size();
        var reference = "";

        if (surveyCount == 1)
            reference = "Correct Survey " + surveyIds.get(0);
        else if (surveyCount < 5)
            reference = "Correct Surveys (" + surveyCount + ") " + survey;
        else
            reference = "Correct Surveys (" + surveyCount + ") " + survey.substring(0, 50) + "...";

        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference(reference)
                .surveyIds(surveyIds)
                .status(StatusJobType.CORRECTED)
                .isExtendedSize(true)
                .program(program)
                .creator(user.get())
                .build();

        job = stagedJobRepository.save(job);

        try {

            logMessage(job, "Correct Survey " + surveyIds);

            var rows = bodyDto.getRows();
            var results = mapRows(rows);
            var result = validate(programValidation, surveyIds, results).getAll();
            var mappedRows = results.stream().map(r -> r.getLeft()).collect(Collectors.toList());
            var validatedMappedRows = validateLatLon(mappedRows);
            var blockingErrors = result.stream().filter(r -> r.getLevelId() == ValidationLevel.BLOCKING)
                    .collect(Collectors.toList());

            if (blockingErrors.size() > 0) {
                logMessage(job, "Survey correction failed. Errors found.");
                return ResponseEntity.ok().body(result);
            }

            var summary = surveyCorrectionService.diffSurveyCorrections(surveyIds, rows);

            surveyCorrectionService.correctSurvey(job, surveyIds, validatedMappedRows);

            try {

                var summaryLog = StagedJobLog.builder()
                        .stagedJob(job)
                        .eventType(StagedJobEventType.SUMMARY)
                        .summary(summary).build();

                stagedJobLogRepository.save(summaryLog);

            } catch (Exception e) {

                logger.error("Correction Diff Summary Failed", e);

                var log = StagedJobLog.builder()
                        .stagedJob(job)
                        .details(e.getMessage())
                        .eventType(StagedJobEventType.ERROR).build();

                stagedJobLogRepository.save(log);
            }

            materializedViewService.refreshAllAsync();

        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details(e.getMessage())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(job.getId());
    }


    @DeleteMapping("correct/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> submitSurveyDeletion(
            @PathVariable Integer id,
            Authentication authentication) {

        var user = secUserRepository.findByEmail(authentication.getName());

        var surveyOptional = surveyRepository.findById(id);
        if (!surveyOptional.isPresent())
            return ResponseEntity.notFound().build();

        var survey = surveyOptional.get();

        if (survey.getLocked() != null && survey.getLocked())
            return ResponseEntity.badRequest().body("{\"message\" : \"Deletion Failed. Survey is locked.\"}");

        if (survey.getPqCatalogued() != null && survey.getPqCatalogued())
            return ResponseEntity.badRequest()
                    .body("{\"message\" : \"Deletion Failed. PQs catalogued for this survey.\"}");

        userActionAuditRepository.save(new UserActionAudit("correction/delete", "survey: " + id));

        var job = stagedJobRepository.save(StagedJob.builder().source(SourceJobType.CORRECTION)
                .reference("Delete Survey " + id.toString()).status(StatusJobType.CORRECTED)
                .program(survey.getProgram())
                .creator(user.get()).build());

        logMessage(job, "Delete Survey " + id);

        try {
            surveyCorrectionService.deleteSurvey(job, survey, Collections.emptyList());
            materializedViewService.refreshAllAsync();
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

    @PostMapping("searchSpecies")
    public ResponseEntity<?> getSpeciesSurveysLocations(
            Authentication authentication,
            @RequestBody SpeciesSearchBodyDto bodyDto) {

        try {
            if (bodyDto == null) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"message\":\"Missing body in the request.\"}");
            }

            if (bodyDto.getLocationIds() == null) {
                bodyDto.setLocationIds(new ArrayList<>());
            }

            var withLocation = bodyDto.getLocationIds().size() > 0;
            var species = observableItemRepository.getForSpeciesSurveysAndLocationsKML(
                    bodyDto.getStartDate(),
                    bodyDto.getEndDate(),
                    withLocation,
                    bodyDto.getLocationIds(),
                    bodyDto.getObservableItemId(),
                    bodyDto.getGeometry());

            return ResponseEntity.ok().body(species);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\":\"Query failed\"}");
        }
    }

    @PostMapping("searchSpeciesSummary")
    public ResponseEntity<?> getSpeciesSurveysLocationsSummary(
            Authentication authentication,
            @RequestBody SpeciesSearchBodyDto bodyDto) {

        try {
            if (bodyDto == null) {
                return ResponseEntity
                        .badRequest()
                        .body("{\"message\":\"Missing body in the request.\"}");
            }

            if (bodyDto.getLocationIds() == null) {
                bodyDto.setLocationIds(new ArrayList<>());
            }

            var withLocation = bodyDto.getLocationIds().size() > 0;
            var species = observableItemRepository.getCorrectionsSummary(
                    bodyDto.getStartDate(),
                    bodyDto.getEndDate(),
                    withLocation,
                    bodyDto.getLocationIds(),
                    bodyDto.getObservableItemId(),
                    bodyDto.getGeometry());

            return ResponseEntity.ok().body(species);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\":\"Query failed\"}");
        }
    }

    @PostMapping("correctSpecies")
    public ResponseEntity<?> updateSpeciesInSurveys(
            Authentication authentication,
            @RequestBody SpeciesCorrectBodyDto bodyDto) throws IOException {

        var curr = observableItemRepository.findById(bodyDto.getPrevObservableItemId()).get();
        var next = observableItemRepository.findById(bodyDto.getNewObservableItemId()).get();

        var auditMessage = "species correction: " + authentication.getName();
        userActionAuditRepository.save(new UserActionAudit("correctSpecies", auditMessage));

        var referenceMessage = "Correct Species from " + curr.getObservableItemName() + " to "
                + next.getObservableItemName();

        var user = secUserRepository.findByEmail(authentication.getName());
        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference(referenceMessage)
                .status(StatusJobType.CORRECTED)
                .creator(user.get())
                .surveyIds(bodyDto.getSurveyIds())
                .program(programRepository.getNoneProgram())
                .build();

        job = stagedJobRepository.save(job);

        var result = new SpeciesCorrectResultDto();
        result.setMessage("Species update success.");

        try {
            var count = surveyCorrectionService.correctSpecies(job, bodyDto.getSurveyIds(), curr, next);

            stagedJobLogRepository.save(StagedJobLog.builder()
                    .stagedJob(job)
                    .eventType(StagedJobEventType.CORRECTING)
                    .details("Updating " + count + " observations.").build());

            stagedJobLogRepository.save(StagedJobLog.builder()
                    .stagedJob(job)
                    .eventType(StagedJobEventType.FILTER)
                    .filterSet(bodyDto.getFilterSet()).build());

            materializedViewService.refreshAllAsync();
        } catch (ConstraintViolationException cv) {
            logger.error("Correction failed on update species, whole transaction rollback!", cv);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Unique constraint error -> " + cv.getConstraintName())
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            // Contain a json of violated id
            result.setMessage("Correction failed due to survey violate unique constraint");
            result.setCurrentSpeciesName(curr.getObservableItemName());
            result.setNextSpeciesName(next.getObservableItemName());
            result.setSurveyIds(objectMapper.readValue(cv.getMessage().getBytes(StandardCharsets.UTF_8), Integer[].class));

            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error attempting species correction")
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);
            result.setMessage("Species failed to update. No data has been changed.");

            return ResponseEntity.badRequest().body(result);
        }

        result.setJobId(job.getId());
        return ResponseEntity.ok().body(result);
    }


    private List<StagedRowFormatted> validateLatLon(List<StagedRowFormatted> mappedRows) {
        if (mappedRows == null || mappedRows.isEmpty()) {
            return mappedRows;
        }
        var resultRows = new ArrayList<StagedRowFormatted>();
        for (var row : mappedRows) {
            var site = row.getSite();
            var distance = SpacialUtil.getDistanceLatLongMeters(site.getLatitude(), site.getLongitude(), row.getLatitude(), row.getLongitude());

            if (distance < SURVEY_LOCATION_TOLERANCE) {
                row.setLatitude(null);
                row.setLongitude(null);
            } else {
                row.setLatitude(Precision.round(row.getLatitude(), COORDINATE_VALID_DECIMAL_COUNT));
                row.setLongitude(Precision.round(row.getLongitude(), COORDINATE_VALID_DECIMAL_COUNT));
            }
            resultRows.add(row);
        }
        return resultRows;
    }


    private Collection<StagedRow> validateLatLonInDiff(Collection<StagedRow> rows, Collection<SurveyValidationError> errors) {
        var resultRows = new ArrayList<>(rows);
        for (var error : errors) {
            if (error.getMessage().contains("This row will use the site's coordinates.")) {
                var rowId = error.getRowIds().iterator().next();
                var row = resultRows.stream().filter(r -> r.getId().equals(rowId)).findFirst().orElse(null);
                if (row != null) {
                    row.setLatitude(null);
                    row.setLongitude(null);
                }
                continue;
            }
            if (error.getMessage().contains("Longitude will be rounded to 5 decimal places")) {
                var rowId = error.getRowIds().iterator().next();
                var row = resultRows.stream().filter(r -> r.getId().equals(rowId)).findFirst().orElse(null);
                if (row != null) {
                    row.setLongitude(String.valueOf(Precision.round(Double.parseDouble(row.getLongitude()), COORDINATE_VALID_DECIMAL_COUNT)));
                }
                continue;
            }
            if (error.getMessage().contains("Latitude will be rounded to 5 decimal places")) {
                var rowId = error.getRowIds().iterator().next();
                var row = resultRows.stream().filter(r -> r.getId().equals(rowId)).findFirst().orElse(null);
                if (row != null) {
                    row.setLatitude(String.valueOf(Precision.round(Double.parseDouble(row.getLatitude()), COORDINATE_VALID_DECIMAL_COUNT)));
                }
            }
        }
        return resultRows;
    }
}