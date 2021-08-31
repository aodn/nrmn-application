package au.org.aodn.nrmn.restapi.validation.process;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;

public class ValidationResultSet {

    Map<String, ValidationError> errorMap = new HashMap<String, ValidationError>();

    public void addGlobal(Collection<ValidationRow> validationRows) {
        for (ValidationRow validationRow : validationRows) {
            ValidationError value = errorMap.getOrDefault(validationRow.getMessage(), new ValidationError(ValidationCategory.GLOBAL, validationRow.getLevelId(), validationRow.getMessage(), null, validationRow.getRowIds(), null));
            if (value != null) {
                value.getRowIds().addAll(validationRow.getRowIds());
                value.setRowIds(value.getRowIds().stream().distinct().collect(Collectors.toList()));
            }
            errorMap.put(validationRow.getKey(), value);
        }
    }

    public void add(Long id, ValidationLevel validationLevel, String column, String message) {
        add(id, ValidationCategory.DATA, validationLevel, column, message, null, false);
    }

    private void add(Long id, ValidationCategory validationCategory, ValidationLevel validationLevel, String column, String message, String value, Boolean groupInRow) {
        String key = (groupInRow) ? message + Long.toString(id) : message + column;
        ValidationError error = errorMap.getOrDefault(key,
                new ValidationError(validationCategory, validationLevel, message, value, new HashSet<>(Arrays.asList(id)), new HashSet<>(Arrays.asList(column))));
        if (error != null) {
            error.getRowIds().add(id);
            error.getColumnNames().add(column);
        }
        errorMap.put(key, error);
    }

    public void add(ValidationCell cell, Boolean groupInRow) {
        if(cell != null)
            add(cell.getRowId(), cell.getCategoryId(), cell.getLevelId(), cell.getColumnName(), cell.getMessage(), cell.getValue(), groupInRow);
    }

    public void addAll(Collection<ValidationCell> cells) {
        addAll(cells, true);
    }

    public void addAll(Collection<ValidationCell> cells, Boolean groupInRow) {
        for (ValidationCell cell : cells) {
            add(cell.getRowId(), cell.getCategoryId(), cell.getLevelId(), cell.getColumnName(), cell.getMessage(), cell.getValue(), groupInRow);
        }
    }

    public Collection<ValidationError> getAll() {
        return errorMap.values();
    }
}
