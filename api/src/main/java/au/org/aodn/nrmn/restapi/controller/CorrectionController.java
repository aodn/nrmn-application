package au.org.aodn.nrmn.restapi.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private ValidationResultSet validate(ProgramValidation programValidation, Boolean isExtended,
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
                validation.addAll(measurementValidation.validate(speciesAttrib.get(), row, isExtended), false);

            // Total Checksum & Missing Data
            validation.addAll(measurementValidation.validateMeasurements(programValidation, row), false);

            // Row Method is valid for species
            validation.add(surveyValidation.validateSpeciesBelowToMethod(row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            validation.add(surveyValidation.validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            validation.add(surveyValidation.validateDateRange(programValidation, row), false);

            // Site distance validation
            validation.add(siteValidation.validateSurveyAtSite(row));
        }

        validation.addAll(surveyValidation.validateSurveys(programValidation, isExtended, mappedRows));

        validation.addAll(surveyValidation.validateSurveyGroups(programValidation, true, mappedRows));

        long errorId = 0;
        for (var error : validation.getAll())
            error.setId(errorId++);

        return validation;
    }

    @GetMapping(path = "correct")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> getSurveyCorrections(@RequestParam("surveyIds") List<Integer> surveyIds) {

        var programs = correctionRowRepository.findProgramsBySurveyIds(surveyIds)
                .stream()
                .collect(Collectors.toList());

        var programValidations = programs.stream().map(ProgramValidation::fromProgram)
                .collect(Collectors.toList());

        if (programValidations.size() != 1)
            return ResponseEntity.badRequest().body("Surveys must share the same program validation");
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
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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

            errors.addAll(dataValidation.checkFormatting(programValidation, bodyDto.getIsExtended(), false,
                    siteCodes, observableItems, rows));

            errors.addAll(validate(programValidation, bodyDto.getIsExtended(), mappedRows).getAll());

            response.setErrors(errors);
        } catch (Exception e) {
            logger.error("Validation Failed", e);
            return ResponseEntity.badRequest().body("Validation failed. Error: " + e.getMessage());
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "correct")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> submitSurveyCorrection(
            @RequestParam("surveyIds") List<Integer> surveyIds,
            Authentication authentication,
            @RequestBody CorrectionRequestBodyDto bodyDto) {

        var user = secUserRepository.findByEmail(authentication.getName());
        var survey = surveyIds.stream().map(Object::toString).collect(Collectors.joining(", "));
        var program = programRepository.findById(bodyDto.getProgramId()).get();
        var programValidation = ProgramValidation.fromProgram(program);

        var message = "correction: username: " + authentication.getName();
        userActionAuditRepository.save(new UserActionAudit("correct/survey", message));

        var job = StagedJob.builder()
                .source(SourceJobType.CORRECTION)
                .reference("Correct " + survey)
                .status(StatusJobType.CORRECTED)
                .program(program)
                .creator(user.get())
                .build();

        job = stagedJobRepository.save(job);

        try {

            logMessage(job, "Correct Survey " + surveyIds);

            var results = mapRows(bodyDto.getRows());
            var result = validate(programValidation, bodyDto.getIsExtended(), results).getAll();
            var mappedRows = results.stream().map(r -> r.getLeft()).collect(Collectors.toList());
            var blockingErrors = result.stream().filter(r -> r.getLevelId() == ValidationLevel.BLOCKING)
                    .collect(Collectors.toList());

            if (blockingErrors.size() > 0) {
                logMessage(job, "Survey correction failed. Errors found.");
                return ResponseEntity.ok().body(result);
            }

            surveyCorrectionService.correctSurvey(job, surveyIds, mappedRows);
            materializedViewService.refreshAllMaterializedViews();

        } catch (Exception e) {

            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error attempting correction")
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

    @GetMapping("correctSpecies")
    public ResponseEntity<?> getSpeciesForSurveysDateAndLocation(
            @RequestParam(value = "startDate") String startDate,
            @RequestParam(value = "endDate") String endDate,
            @RequestParam(value = "locationId", required = false) Integer locationId,
            @RequestParam(value = "observableItemId", required = false) Integer observableItemId,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "country", required = false) String country) {
        var species = observableItemRepository.getAllDistinctForSurveys(
                startDate,
                endDate,
                locationId,
                observableItemId,
                state,
                country);
        return ResponseEntity.ok().body(species);
    }

    @PostMapping("correctSpecies")
    public ResponseEntity<?> updateSpeciesInSurveys(
            Authentication authentication,
            @RequestBody SpeciesCorrectBodyDto bodyDto) {

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
                .program(programRepository.getNoneProgram())
                .build();

        job = stagedJobRepository.save(job);

        try {

            surveyCorrectionService.correctSpecies(job, bodyDto.getSurveyIds(), curr, next);

            materializedViewService.refreshAllMaterializedViews();

        } catch (Exception e) {
            logger.error("Correction Failed", e);

            var log = StagedJobLog.builder()
                    .stagedJob(job)
                    .details("Application error attempting species correction")
                    .eventType(StagedJobEventType.ERROR).build();

            stagedJobLogRepository.save(log);

            return ResponseEntity.badRequest().body("Species failed to update. No data has been changed.");
        }

        return ResponseEntity.ok().body(job.getId());
    }
}
