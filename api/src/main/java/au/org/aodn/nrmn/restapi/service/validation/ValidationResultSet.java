package au.org.aodn.nrmn.restapi.service.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;

public class ValidationResultSet {

    Map<String, SurveyValidationError> errorMap = new HashMap<String, SurveyValidationError>();

    public void add(SurveyValidationError validationRow) {
        if(validationRow == null) return;
        var validationRowKey = validationRow.getMessage();
        var value = errorMap.getOrDefault(validationRowKey, new SurveyValidationError(ValidationCategory.DATA, validationRow.getLevelId(), validationRow.getMessage(), validationRow.getRowIds(), validationRow.getColumnNames()));
        if (value != null) {
            var rowIds = Stream.concat(value.getRowIds().stream(), validationRow.getRowIds().stream());
            value.setRowIds(rowIds.distinct().collect(Collectors.toList()));
            value.setRowIds(value.getRowIds().stream().distinct().collect(Collectors.toList()));
        }
        errorMap.put(validationRowKey, value);
    }

    public void addAll(Collection<SurveyValidationError> validationRows) {
        for (SurveyValidationError validationRow : validationRows) {
            add(validationRow);
        }
    }

    public void add(Long id, ValidationLevel validationLevel, String column, String message) {
        add(id, ValidationCategory.DATA, validationLevel, column, message, false);
    }

    private void add(Long id, ValidationCategory validationCategory, ValidationLevel validationLevel, String column, String message, Boolean groupInRow) {
        var key = (groupInRow) ? message + Long.toString(id) : message + column;
        var defaultValue = new SurveyValidationError(validationCategory, validationLevel, message, new HashSet<>(Arrays.asList(id)), new HashSet<>(Arrays.asList(column)));
        var value = errorMap.getOrDefault(key, defaultValue);
        if (value != null) {
            value.getRowIds().add(id);
            value.getColumnNames().add(column);
        }
        errorMap.put(key, value);
    }

    public void add(ValidationCell cell, Boolean groupInRow) {
        if (cell != null)
            add(cell.getRowId(), cell.getCategoryId(), cell.getLevelId(), cell.getColumnName(), cell.getMessage(), groupInRow);
    }

    public void addAll(Collection<ValidationCell> cells, Boolean groupInRow) {
        for (ValidationCell cell : cells) {
            add(cell.getRowId(), cell.getCategoryId(), cell.getLevelId(), cell.getColumnName(), cell.getMessage(), groupInRow);
        }
    }

    public Collection<SurveyValidationError> getAll() {
        return errorMap.values();
    }
}
