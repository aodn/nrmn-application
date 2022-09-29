package au.org.aodn.nrmn.restapi.service.validation;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.formatting.SpeciesFormattingService;

@Component
public class ValidationProcess {

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    StagedRowRepository rowRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    DataValidation dataValidation;

    @Autowired
    MeasurementValidation speciesMeasurement;

    @Autowired
    SpeciesFormattingService speciesFormatting;

    private static final Integer[] METHODS_TO_CHECK = { 0, 1, 2, 7, 10 };
    private static final LocalDate DATE_MIN_RLS = LocalDate.parse("2006-01-01");
    private static final LocalDate DATE_MIN_ATRC = LocalDate.parse("1991-01-01");

    private ValidationCell validateSpeciesBelowToMethod(StagedRowFormatted row) {

        if (row.getSpecies().isPresent() && row.getSpecies().get().getMethods() != null) {
            var methodIds = row.getSpecies().get().getMethods().stream().map(m -> m.getMethodId())
                    .collect(Collectors.toSet());

            if (!methodIds.contains(row.getMethod()))
                return new ValidationCell(
                        ValidationCategory.DATA, ValidationLevel.WARNING, "Method " + row.getMethod()
                                + " invalid for species " + row.getSpecies().get().getObservableItemName(),
                        row.getId(), "method");

        }
        return null;
    }

    private ValidationCell validateInvertsZeroOnM3M4M5(StagedRowFormatted row) {
        return (row.getMethod() != null && row.getInverts() != null && Arrays.asList(3, 4, 5).contains(row.getMethod())
                && row.getInverts() > 0)
                        ? new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                                "Method " + row.getMethod() + " has value for inverts", row.getId(), "inverts")
                        : null;
    }

    private SurveyValidationError validateSurveyIsNew(StagedRowFormatted row) {
        if (row.getDate() != null && Arrays.asList(METHODS_TO_CHECK).contains(row.getMethod())) {

            var surveyDate = Date.from(row.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            var existingSurveys = surveyRepository.findBySiteDepthSurveyNumDate(row.getSite(), row.getDepth(),
                    row.getSurveyNum(), surveyDate);

            if (!existingSurveys.isEmpty()) {
                var existingSurvey = existingSurveys.stream().findFirst().get();
                var message = "Survey exists: " + existingSurvey.getSurveyId() + " includes "
                        + row.getDecimalSurvey();
                return new SurveyValidationError(ValidationCategory.DATA, ValidationLevel.BLOCKING, message,
                        Arrays.asList(row.getId()), Arrays.asList("siteCode"));
            }

        }
        return null;
    }

    // VALIDATION: Survey coordinates match site coordinates
    private Collection<ValidationCell> validateSurveyAtSite(StagedRowFormatted row) {
        var errors = new ArrayList<ValidationCell>();

        if (row.getSite() == null || row.getSite().getLatitude() == null || row.getSite().getLongitude() == null
                || row.getLatitude() == null || row.getLongitude() == null)
            return errors;

        var distMeters = getDistanceLatLongMeters(row.getSite().getLatitude(), row.getSite().getLongitude(),
                row.getLatitude(), row.getLongitude());

        // Warn if survey is more than 10 meters away from site
        if (distMeters > 10) {
            String message = "Survey coordinates more than 10m from site (" + String.format("%.1f", distMeters) + "m)";
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, message, row.getId(),
                    "latitude"));
            errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, message, row.getId(),
                    "longitude"));
        }

        return errors;
    }

    private ValidationCell validateDateRange(LocalDate earliest, StagedRowFormatted row) {

        if (row.getDate() == null)
            return null;

        // Validation: Surveys Too Old
        if (row.getDate().isAfter(LocalDate.from(ZonedDateTime.now())))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, "Date is in the future",
                    row.getId(), "date");

        // Validation: Future Survey Rule
        if (row.getDate().isBefore(earliest))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                    "Date must be after " + earliest.toString(), row.getId(), "date");

        return null;
    }

    public SurveyValidationError validateMethod3Quadrats(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();
        var rowIds = new HashSet<Long>();

        for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
            if (rows.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() == 0) {
                rowIds.addAll(rows.stream().map(r -> r.getId()).collect(Collectors.toList()));
                columnNames.add(Integer.toString(measureIndex));
            }

        return rowIds.size() > 0 ? new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Missing quadrats in transect " + transect, rowIds, columnNames) : null;
    }

    public Collection<ValidationCell> validateMethod3QuadratsLT50(List<StagedRowFormatted> rows) {
        var errors = new ArrayList<ValidationCell>();

        for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
            for (var row : rows) {
                if (row.getMeasureJson().getOrDefault(measureIndex, 0) > 50)
                    errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                            "M3 quadrat more than 50", row.getId(), Integer.toString(measureIndex)));
            }

        return errors;
    }

    public SurveyValidationError validateMethod3QuadratsGT50(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();

        for (var measureIndex : Arrays.asList(1, 2, 3, 4, 5)) {
            if (rows.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() < 50)
                columnNames.add(Integer.toString(measureIndex));
        }

        var rowIds = rows.stream().map(r -> r.getId()).collect(Collectors.toList());
        return columnNames.size() > 0 ? new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Quadrats do not sum to at least 50 in transect " + transect, rowIds, columnNames) : null;
    }

    public SurveyValidationError validateSurveyTransectNumber(List<StagedRowFormatted> surveyRows) {
        var invalidTransectRows = surveyRows.stream()
                .filter(r -> !Arrays.asList(1, 2, 3, 4).contains(r.getSurveyNum())).collect(Collectors.toList());
        if (invalidTransectRows.size() > 0)
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Survey group transect invalid",
                    invalidTransectRows.stream().map(r -> r.getId()).collect(Collectors.toList()),
                    Arrays.asList("depth"));
        return null;
    }

    private SurveyValidationError validateSurveyComplete(ProgramValidation validation, List<StagedRowFormatted> surveyRows) {

        if (surveyRows.stream().anyMatch(r -> r.getMethod() == null || r.getBlock() == null))
            return null;

        var messagePrefix = "Survey incomplete: " + surveyRows.get(0).getDecimalSurvey();

        var surveyByMethod = surveyRows.stream().filter(sr -> sr.getMethod() != null && sr.getBlock() != null)
                .collect(Collectors.groupingBy(StagedRowFormatted::getMethod));

        var rowIds = new HashSet<Long>();
        var flagColumns = new HashSet<String>();
        var messages = new ArrayList<String>();

        // VALIDATION: If method = 0 then Block should be 0, 1 or 2
        var method0Rows = surveyByMethod.get(0);
        if (method0Rows != null && method0Rows.stream().anyMatch(r -> !Arrays.asList(0, 1, 2).contains(r.getBlock())))
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Method 0 must have block 0, 1 or 2",
                    method0Rows.stream().map(r -> r.getId()).collect(Collectors.toList()), Arrays.asList("block"));

        // VALIDATION: Both M1, M2 present and if ATRC has M3 and at least one method of
        // 3,4,5,7
        var requiredMethods = validation == ProgramValidation.ATRC ? Arrays.asList(1, 2, 3) : Arrays.asList(1, 2);
        var missingMethods = new ArrayList<Integer>(requiredMethods);
        missingMethods.removeAll(surveyByMethod.keySet());
        if (missingMethods.size() > 0) {
            var missingMethodsList = missingMethods.stream().map(m -> m.toString()).collect(Collectors.toList());
            messages.add("missing M" + String.join(", M", missingMethodsList));
            rowIds.addAll(surveyRows.stream().map(r -> r.getId()).collect(Collectors.toList()));
            flagColumns.add("method");
        }

        // VALIDATION: M1, M2 each has B1, B2 and if ATRC M3 has B0
        var methodsRequired = validation == ProgramValidation.RLS ? Arrays.asList(1, 2) : Arrays.asList(1, 2, 3);
        var level = ValidationLevel.WARNING;
        for (var method : methodsRequired) {
            var methodRows = surveyByMethod.get(method);
            if (methodRows == null)
                continue;

            var blocksRequired = validation == ProgramValidation.RLS ? new ArrayList<Integer>(Arrays.asList(1, 2))
                    : new ArrayList<Integer>(method == 3 ? Arrays.asList(0) : Arrays.asList(1, 2));

            var hasBlocks = methodRows.stream().map(r -> r.getBlock()).distinct().collect(Collectors.toList());
            var missingBlocks = blocksRequired.stream().filter(b -> !hasBlocks.contains(b))
                    .collect(Collectors.toList());

            if (missingBlocks.size() > 0) {
                if (method == 3) {
                    level = ValidationLevel.BLOCKING;
                    messages.add("M3 " + (hasBlocks.size() > 0 ? "recorded on wrong block" : "missing B0"));
                } else {
                    messages.add("M" + method + " missing B" + String.join(", ",
                            missingBlocks.stream().map(m -> m.toString()).collect(Collectors.toList())));
                }
                rowIds.addAll(methodRows.stream().map(r -> r.getId()).collect(Collectors.toList()));
                flagColumns.add("block");
            }
        }

        if (messages.size() > 0) {
            return new SurveyValidationError(ValidationCategory.SPAN, level,
                    messagePrefix + " " + String.join(". ", messages), rowIds, flagColumns);
        }

        return null;
    }

    public SurveyValidationError validateSurveyGroup(List<StagedRowFormatted> surveyRows) {
        var surveyGroup = surveyRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyNum));
        if (!surveyGroup.keySet().containsAll(Arrays.asList(1, 2, 3, 4))) {
            var missingSurveys = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
            missingSurveys.removeAll(surveyGroup.keySet());
            var missingSurveysMessage = missingSurveys.stream().map(s -> s.toString()).collect(Collectors.toList());
            var row = surveyRows.get(0).getRef();
            var message = "Survey group " + row.getSurveyGroup() + " missing transect "
                    + String.join(", ", missingSurveysMessage);
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING, message,
                    surveyRows.stream().map(r -> r.getId()).collect(Collectors.toList()), Arrays.asList("depth"));
        }
        return null;
    }

    private Collection<SurveyValidationError> checkSurveys(ProgramValidation validation, Boolean isExtended,
            Map<String, List<StagedRowFormatted>> surveyMap) {
        var res = new HashSet<SurveyValidationError>();

        for (var survey : surveyMap.entrySet()) {
            var surveyRows = survey.getValue();

            if (validation == ProgramValidation.ATRC) {
                // VALIDATION: Survey group transect number valid
                res.add(validateSurveyTransectNumber(surveyRows));
            }

            // VALIDATION: Survey Complete
            res.add(validateSurveyComplete(validation, surveyRows));

            // VALIDATION: Is Existing Survey
            res.add(validateSurveyIsNew(surveyRows.get(0)));
        }

        res.remove(null);

        return res;
    }

    private Collection<SurveyValidationError> checkSurveyGroups(ProgramValidation validation, Boolean isExtended,
            Map<String, List<StagedRowFormatted>> surveyGroupMap) {
        var res = new HashSet<SurveyValidationError>();

        for (var survey : surveyGroupMap.entrySet()) {
            var surveyRows = survey.getValue();

            if (validation == ProgramValidation.ATRC) {
                // VALIDATION: Survey Group Complete
                res.add(validateSurveyGroup(surveyRows));
            }
        }

        res.remove(null);

        return res;
    }

    private Collection<SurveyValidationError> checkMethod3Transects(Boolean isExtended,
            Map<String, List<StagedRowFormatted>> method3SurveyMap) {
        var res = new HashSet<SurveyValidationError>();
        var results = new ValidationResultSet();

        // Validate M3 transects
        for (String transectName : method3SurveyMap.keySet()) {
            res.add(validateMethod3Quadrats(transectName, method3SurveyMap.get(transectName)));
            res.add(validateMethod3QuadratsGT50(transectName, method3SurveyMap.get(transectName)));
            results.addAll(validateMethod3QuadratsLT50(method3SurveyMap.get(transectName)), false);
        }

        res.addAll(results.getAll());
        res.remove(null);

        return res;
    }

    public Collection<SurveyValidationError> checkData(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> rows) {

        var results = new ValidationResultSet();

        var dateValidation = validation == ProgramValidation.RLS ? DATE_MIN_RLS : DATE_MIN_ATRC;

        /** Row-level Checks */
        for (var row : rows) {

            // Validate measurements if species attributes are present
            var speciesAttrib = row.getSpeciesAttributesOpt();
            if (speciesAttrib.isPresent())
                results.addAll(speciesMeasurement.validate(speciesAttrib.get(), row, isExtended), false);

            // Total Checksum & Missing Data
            results.addAll(speciesMeasurement.validateMeasurements(validation, row), false);

            // Row Method is valid for species
            results.add(validateSpeciesBelowToMethod(row), false);

            // Validate survey is at site location
            results.addAll(validateSurveyAtSite(row), false);

            // Validate M3, M4 and M5 rows have zero inverts
            results.add(validateInvertsZeroOnM3M4M5(row), false);

            // Date is not in the future or too far in the past
            results.add(validateDateRange(dateValidation, row), false);
        }

        var res = new HashSet<SurveyValidationError>();
        res.addAll(results.getAll());
        res.remove(null);
        return res;
    }

    public ValidationResponse generateSummary(Collection<StagedRowFormatted> mappedRows) {
        var response = new ValidationResponse();
        response.setRowCount(mappedRows.size());

        var distinctSites = mappedRows.stream().map(r -> r.getRef().getSiteCode().toUpperCase())
                .filter(s -> s.length() > 0).distinct().collect(Collectors.toList());
        var distinctSitesExisting = mappedRows.stream().filter(r -> r.getSite() != null)
                .map(r -> r.getSite().getSiteCode().toUpperCase()).distinct().collect(Collectors.toList());
        response.setSiteCount(distinctSites.size());

        var foundSites = new HashMap<String, Boolean>();
        distinctSites.stream().forEach(s -> foundSites.put(s, !distinctSitesExisting.contains(s)));
        response.setFoundSites(foundSites);
        response.setNewSiteCount(foundSites.values().stream().filter(e -> e == true).count());

        // Diver Count

        var divers = diverRepository.getAll();

        var distinctSurveyDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getDiver(), Normalizer.Form.NFD)
                        .replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> d.length() > 0).distinct().collect(Collectors.toList());
        var distinctPQDivers = mappedRows.stream()
                .map(d -> Normalizer.normalize(d.getRef().getPqs(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                        .toUpperCase())
                .filter(d -> d.length() > 0 && !d.equalsIgnoreCase("0")).distinct().collect(Collectors.toList());
        var distinctBuddies = mappedRows.stream().flatMap(r -> Stream.of(r.getRef().getBuddy().split(",")))
                .map(d -> Normalizer.normalize(d, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                .filter(d -> d.length() > 0).distinct().collect(Collectors.toList());
        distinctSurveyDivers.addAll(distinctPQDivers);
        distinctSurveyDivers.addAll(distinctBuddies);

        // Map diver full names to initials and then use the distinct count of initials
        // to determine the number of distinct divers
        var distinctDiverInitials = distinctSurveyDivers.stream().map(s -> {
            var diver = divers.stream()
                    .filter(d -> StringUtils.isNotEmpty(d.getFullName())
                            && Normalizer.normalize(d.getFullName(), Normalizer.Form.NFD)
                                    .replaceAll("[^\\p{ASCII}]", "").equalsIgnoreCase(s))
                    .findFirst();
            return diver.isPresent() ? diver.get().getInitials() : s;
        }).distinct().collect(Collectors.toList());

        var totalDistinctDivers = distinctDiverInitials.size();

        distinctDiverInitials.removeIf(
                n -> divers.stream().anyMatch(d -> d.getInitials() != null && d.getInitials().equalsIgnoreCase(n)));

        var totalNewDivers = distinctDiverInitials.size();

        response.setDiverCount(totalDistinctDivers);
        response.setNewDiverCount(totalNewDivers);

        // End Diver Count

        var obsItemNames = mappedRows.stream().map(r -> r.getRef().getSpecies().toUpperCase())
                .filter(r -> r.length() > 0).distinct().collect(Collectors.toList());
        var distinctObsItems = obsItemNames.size();
        var distinctObsItemsExisting = mappedRows.stream().filter(r -> r.getSpecies().isPresent())
                .map(r -> r.getSpecies().get().getObservableItemName()).distinct().count();
        var distinctNotPresentObsItem = mappedRows.stream().map(r -> r.getRef().getSpecies().toUpperCase())
                .filter(r -> r.equalsIgnoreCase("SURVEY NOT DONE")).distinct().count();
        response.setNewObsItemCount(distinctObsItems - distinctObsItemsExisting - distinctNotPresentObsItem);

        response.setObsItemCount(distinctObsItems);
        return response;
    }

    public ValidationResponse process(StagedJob job) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var hasAdminRole = authentication != null
                ? authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))
                : false;

        var rows = rowRepository.findRowsByJobId(job.getId());

        var validation = ProgramValidation.fromProgram(job.getProgram());

        var enteredSiteCodes = rows.stream().map(s -> s.getSiteCode().toUpperCase()).collect(Collectors.toSet());
        var siteCodes = siteRepository.getAllSiteCodesMatching(enteredSiteCodes);
        var sheetErrors = new HashSet<SurveyValidationError>();

        var species = speciesFormatting.getSpeciesForRows(rows);
        sheetErrors.addAll(dataValidation.checkFormatting(validation, job.getIsExtendedSize(), siteCodes, species, rows));
        var mappedRows = speciesFormatting.formatRowsWithSpecies(rows, species);

        var response = generateSummary(mappedRows);

        if (hasAdminRole) {
            response.setErrors(sheetErrors);
            return response;
        }

        sheetErrors.addAll(checkData(validation, job.getIsExtendedSize(), mappedRows));

        var surveyMap = mappedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        sheetErrors.addAll(checkSurveys(validation, job.getIsExtendedSize(), surveyMap));
        response.setIncompleteSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey incomplete")).count());
        response.setExistingSurveyCount(
                sheetErrors.stream().filter(e -> e.getMessage().contains("Survey exists:")).count());

        var surveyGroupMap = mappedRows.stream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyGroup));
        sheetErrors.addAll(checkSurveyGroups(validation, job.getIsExtendedSize(), surveyGroupMap));

        var method3SurveyMap = mappedRows.stream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3)
                        && !row.getRef().getSpecies().equalsIgnoreCase("Survey Not Done"))
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        sheetErrors.addAll(checkMethod3Transects(job.getIsExtendedSize(), method3SurveyMap));

        var distinctSurveys = mappedRows.stream().filter(r -> Arrays.asList(1, 2).contains(r.getMethod()))
                .map(r -> r.getSurvey()).distinct().count();
        response.setSurveyCount(distinctSurveys);

        var errorId = 0;
        for (var validationError : sheetErrors)
            validationError.setId(errorId++);

        response.setErrors(sheetErrors);

        return response;
    }
}
