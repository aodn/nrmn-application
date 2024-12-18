package au.org.aodn.nrmn.restapi.service.validation;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.data.model.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;

@Service
public class SurveyValidation {

    @Autowired
    SurveyRepository surveyRepository;

    private static final Collection<Integer> SURVEY_METHODS_TO_CHECK = Arrays.asList(0, 1, 2, 3, 7, 10);

    public ValidationCell validateSpeciesBelowToMethod(Boolean allowM11, StagedRowFormatted row) {

        if (row.getSpecies().isPresent() && row.getSpecies().get().getMethods() != null) {

            var methodIds = row.getSpecies().get().getMethods().stream().map(Method::getMethodId)
                    .collect(Collectors.toSet());

            // Handle all M10 as M1
            var method = row.getMethod();
            var useRowMethod = row.getMethod() == 10 ? 1 : row.getMethod();
            var validMethod = methodIds.contains(useRowMethod) || (useRowMethod == 11 && allowM11);
            if (!validMethod) {
                var level = useRowMethod == 11 ? ValidationLevel.BLOCKING : ValidationLevel.WARNING;
                var message = "Method " + method + " invalid for species "
                        + row.getSpecies().get().getObservableItemName();
                return new ValidationCell(ValidationCategory.DATA, level, message, row.getId(), "method");
            }
        }
        return null;
    }

    public ValidationCell validateInvertsZeroOnM3M4M5(StagedRowFormatted row) {
        return (row.getMethod() != null && row.getInverts() != null && Arrays.asList(3, 4, 5).contains(row.getMethod())
                && row.getInverts() > 0)
                        ? new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                                "Method " + row.getMethod() + " has value for inverts", row.getId(), "inverts")
                        : null;
    }

    public ValidationCell validateDateRange(ProgramValidation v, StagedRowFormatted row) {

        var validation = v == ProgramValidation.RLS ? ProgramValidation.RLS : ProgramValidation.ATRC;

        if (row.getDate() == null)
            return null;

        // Validation: Surveys Too Old
        if (row.getDate().isAfter(LocalDate.from(ZonedDateTime.now())))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING, "Date is in the future",
                    row.getId(), "date");

        // Validation: Future Survey Rule
        if (row.getDate().isBefore(validation.getMinDate()))
            return new ValidationCell(ValidationCategory.DATA, ValidationLevel.WARNING,
                    "Date must be after " + validation.getMinDate().toString(), row.getId(), "date");

        return null;
    }

    public SurveyValidationError validateMethod3Quadrats(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();
        var rowIds = new HashSet<Long>();

        // Make sure we only validate rows where method is 3, we cannot assume income
        // row are all method 3
        var t = rows
                .parallelStream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3))
                .collect(Collectors.toList());

        if (!t.isEmpty()) {
            for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
                if (t.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() == 0) {
                    rowIds.addAll(rows.stream().map(StagedRowFormatted::getId).collect(Collectors.toList()));
                    columnNames.add(Integer.toString(measureIndex));
                }
        }

        return !rowIds.isEmpty() ? new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Missing quadrats in transect " + transect, rowIds, columnNames) : null;
    }

    public Collection<ValidationCell> validateMethod3QuadratsLT50(List<StagedRowFormatted> rows) {
        var errors = new ArrayList<ValidationCell>();

        // Make sure we only validate rows where method is 3, we cannot assume income
        // row are all method 3
        var t = rows
                .parallelStream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3))
                .collect(Collectors.toList());

        if (!t.isEmpty()) {
            for (int measureIndex : Arrays.asList(1, 2, 3, 4, 5))
                for (var row : t) {
                    if (row.getMeasureJson().getOrDefault(measureIndex, 0) > 50)
                        errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING,
                                "M3 quadrat more than 50", row.getId(), Integer.toString(measureIndex)));
                }
        }
        return errors;
    }

    public SurveyValidationError validateMethod3QuadratsGT50(String transect, List<StagedRowFormatted> rows) {

        var columnNames = new HashSet<String>();

        // Make sure we only validate rows where method is 3, we cannot assume income
        // row are all method 3
        var t = rows
                .parallelStream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3))
                .collect(Collectors.toList());

        if (!t.isEmpty()) {
            for (var measureIndex : Arrays.asList(1, 2, 3, 4, 5)) {
                if (t.stream().mapToInt(row -> row.getMeasureJson().getOrDefault(measureIndex, 0)).sum() < 50)
                    columnNames.add(Integer.toString(measureIndex));
            }

        }

        var rowIds = t.parallelStream().map(StagedRowFormatted::getId).collect(Collectors.toList());

        return !columnNames.isEmpty() ? new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                "Quadrats do not sum to at least 50 in transect " + transect, rowIds, columnNames) : null;
    }

    public SurveyValidationError validateSurveyTransectNumber(List<StagedRowFormatted> surveyRows) {
        var invalidTransectRows = surveyRows
                .parallelStream()
                .filter(r -> !Arrays.asList(0, 1, 2, 3, 4).contains(r.getSurveyNum())).collect(Collectors.toList());
        if (!invalidTransectRows.isEmpty())
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Survey group transect invalid",
                    invalidTransectRows.parallelStream().map(StagedRowFormatted::getId).collect(Collectors.toList()),
                    List.of("depth"));
        return null;
    }

    private SurveyValidationError validateSurveyComplete(ProgramValidation validation,
            List<StagedRowFormatted> surveyRows) {

        if (surveyRows.parallelStream().anyMatch(r -> r.getMethod() == null || r.getBlock() == null))
            return null;

        var messagePrefix = "Survey incomplete: " + surveyRows.get(0).getDecimalSurvey();

        var surveyByMethod = surveyRows
                .parallelStream()
                .filter(sr -> sr.getMethod() != null && sr.getBlock() != null)
                .collect(Collectors.groupingBy(StagedRowFormatted::getMethod));

        var rowIds = new HashSet<Long>();
        var flagColumns = new HashSet<String>();
        var messages = new ArrayList<String>();

        // VALIDATION: If method = 0 then Block should be 0, 1 or 2
        var method0Rows = surveyByMethod.get(0);
        if (method0Rows != null && method0Rows.parallelStream().anyMatch(r -> !Arrays.asList(0, 1, 2).contains(r.getBlock())))
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING,
                    "Method 0 must have block 0, 1 or 2",
                    method0Rows.parallelStream().map(StagedRowFormatted::getId).collect(Collectors.toList()), List.of("block"));

        // VALIDATION: M10 requires B1 and B2
        var method10 = surveyByMethod.get(10);
        if (method10 != null &&
                !method10.stream().map(StagedRowFormatted::getBlock).distinct().collect(Collectors.toList()).containsAll(List.of(1, 2)))
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.BLOCKING,
                    "M10 requires B1 and B2",
                    method10.stream().map(StagedRowFormatted::getId).collect(Collectors.toList()), List.of("block"));

        // VALIDATION: M1, M2 (and M3 if ATRC) are present
        var requiredMethods = validation == ProgramValidation.ATRC ? Arrays.asList(1, 2, 3) : Arrays.asList(1, 2);
        var missingMethods = new ArrayList<>(requiredMethods);
        missingMethods.removeAll(surveyByMethod.keySet());
        if (!missingMethods.isEmpty()) {
            var missingMethodsList = missingMethods.parallelStream().map(Object::toString).collect(Collectors.toList());
            messages.add("missing M" + String.join(", M", missingMethodsList));
            rowIds.addAll(surveyRows.parallelStream().map(StagedRowFormatted::getId).collect(Collectors.toList()));
            flagColumns.add("method");
        }

        // VALIDATION: M1, M2 each have B1, B2 (and M3 has B0 if ATRC)
        var methodsRequired = validation == ProgramValidation.RLS ? Arrays.asList(1, 2) : Arrays.asList(1, 2, 3);
        var level = ValidationLevel.WARNING;
        for (var method : methodsRequired) {
            var methodRows = surveyByMethod.get(method);
            if (methodRows == null)
                continue;

            var blocksRequired = validation == ProgramValidation.RLS ? new ArrayList<>(List.of(1, 2))
                    : new ArrayList<>(method == 3 ? List.of(0) : List.of(1, 2));

            var hasBlocks = methodRows.stream().map(StagedRowFormatted::getBlock).distinct().collect(Collectors.toList());
            var missingBlocks = blocksRequired.stream().filter(b -> !hasBlocks.contains(b))
                    .collect(Collectors.toList());

            if (!missingBlocks.isEmpty()) {
                if (method == 3) {
                    level = ValidationLevel.BLOCKING;
                    messages.add("M3 " + (!hasBlocks.isEmpty() ? "recorded on wrong block" : "missing B0"));
                } else {
                    messages.add(String.format("M%s missing B%s", method, missingBlocks.stream().map(Object::toString).collect(Collectors.joining(", "))));
                }
                rowIds.addAll(methodRows.parallelStream().map(StagedRowFormatted::getId).collect(Collectors.toList()));
                flagColumns.add("block");
            }
        }

        if (!messages.isEmpty()) {
            return new SurveyValidationError(ValidationCategory.SPAN, level,
                    messagePrefix + " " + String.join(". ", messages), rowIds, flagColumns);
        }

        return null;
    }

    private SurveyValidationError validateSurveyGroup(List<StagedRowFormatted> surveyRows) {
        var surveyGroup = surveyRows.parallelStream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyNum));
        if (!surveyGroup.keySet().containsAll(List.of(1, 2, 3, 4))) {
            var missingSurveys = new ArrayList<>(List.of(1, 2, 3, 4));
            missingSurveys.removeAll(surveyGroup.keySet());
            var missingSurveysMessage = missingSurveys.stream().map(Object::toString).collect(Collectors.toList());
            var row = surveyRows.get(0).getRef();
            var message = "Survey group " + row.getSurveyGroup() + " missing transect "
                    + String.join(", ", missingSurveysMessage);
            return new SurveyValidationError(ValidationCategory.SPAN, ValidationLevel.WARNING, message,
                    surveyRows.stream().map(StagedRowFormatted::getId).collect(Collectors.toList()), List.of("depth"));
        }
        return null;
    }

    private SurveyValidationError validateSurveyIsNew(StagedRowFormatted row) {
        if (row.getDate() != null && SURVEY_METHODS_TO_CHECK.contains(row.getMethod())) {

            var surveyDate = Date.from(row.getDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            var existingSurveyIds = surveyRepository.findBySiteDepthSurveyNumDate(row.getSite(), row.getDepth(),
                    row.getSurveyNum(), surveyDate);

            if (!existingSurveyIds.isEmpty() && !existingSurveyIds.contains(row.getSurveyId())) {
                var existingSurveyId = existingSurveyIds.get(0);
                var message = "Survey exists: " + existingSurveyId + " includes " + row.getDecimalSurvey();
                var level = (row.getMethod() == 3) ? ValidationLevel.WARNING : ValidationLevel.BLOCKING;
                return new SurveyValidationError(ValidationCategory.DATA, level, message,
                        List.of(row.getId()), List.of("siteCode"));
            }

        }
        return null;
    }

    private Collection<SurveyValidationError> checkSurveys(ProgramValidation validation, Boolean isExtended,
            Map<String, List<StagedRowFormatted>> surveyMap) {
        var res = new HashSet<SurveyValidationError>();

        for (var survey : surveyMap.entrySet()) {
            var surveyRows = survey.getValue();
            var row = surveyRows.get(0);

            if (validation == ProgramValidation.ATRC) {
                // VALIDATION: Survey group transect number valid
                res.add(validateSurveyTransectNumber(surveyRows));
            }

            // VALIDATION: Is Existing Survey
            res.add(validateSurveyIsNew(row));

            // Skip SurveyComplete if M3 and survey exists
            var surveyExistsM3 = row.getMethod() != null && row.getMethod() == 3
                    && res.parallelStream().anyMatch(e -> e != null && e.getMessage().contains("Survey exists:"));

            // VALIDATION: Survey Complete
            if (!surveyExistsM3 && validation != ProgramValidation.NONE) {
                res.add(validateSurveyComplete(validation, surveyRows));
            }
        }

        res.remove(null);

        return res;
    }

    private Collection<SurveyValidationError> checkSurveyGroups(ProgramValidation validation,
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

    public List<String> validateSurveysMatch(List<Integer> surveyIds,
            Collection<StagedRowFormatted> mappedRows) {

        var errors = new ArrayList<String>();

        var rowsGroupedBySurvey = mappedRows
                .parallelStream()
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurveyId));

        var groupedSurveyIds = rowsGroupedBySurvey
                .keySet()
                .parallelStream()
                .map(Long::intValue)
                .collect(Collectors.toList());

        if (!surveyIds.containsAll(groupedSurveyIds)) {
            groupedSurveyIds.removeAll(surveyIds);
            errors.add("Survey IDs created: " + String.join(", ", groupedSurveyIds.parallelStream().map(Object::toString)
                    .collect(Collectors.toList())));
        }

        if (!groupedSurveyIds.containsAll(surveyIds)) {
            surveyIds.removeAll(groupedSurveyIds);
            errors.add("Survey IDs missing: " + String.join(", ", surveyIds.parallelStream().map(Object::toString)
                    .collect(Collectors.toList())));
        }

        return errors;
    }

    public Collection<SurveyValidationError> validateSurveys(ProgramValidation validation, Boolean isExtended,
            Collection<StagedRowFormatted> mappedRows) {

        var surveyMap = mappedRows
                .parallelStream()
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        var sheetErrors = new HashSet<>(checkSurveys(validation, isExtended, surveyMap));

        var method3SurveyMap = mappedRows
                .parallelStream()
                .filter(row -> row.getMethod() != null && row.getMethod().equals(3)
                        && !row.getRef().getSpecies().equalsIgnoreCase("Survey Not Done"))
                .collect(Collectors.groupingBy(StagedRowFormatted::getSurvey));
        sheetErrors.addAll(checkMethod3Transects(isExtended, method3SurveyMap));

        return sheetErrors;
    }

    public Collection<SurveyValidationError> validateSurveyGroups(ProgramValidation validation, Boolean isCorrection,
            Collection<StagedRowFormatted> mappedRows) {
        var sheetErrors = new HashSet<SurveyValidationError>();

        var surveyGroupMap = mappedRows.parallelStream().collect(Collectors.groupingBy(StagedRowFormatted::getSurveyGroup));

        // Skip survey group validation if correcting a single survey
        if (!isCorrection || surveyGroupMap.size() > 1)
            sheetErrors.addAll(checkSurveyGroups(validation, surveyGroupMap));

        return sheetErrors;
    }
}
