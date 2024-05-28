package au.org.aodn.nrmn.restapi.service.validation;


import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Collection;

import static au.org.aodn.nrmn.restapi.util.Constants.COORDINATE_VALID_DECIMAL_COUNT;

/**
 * This class is used to automatically rectify the data during the validation process.
 * e.g. If the lat and lon are more than 5 decimal places, then it will round it to 5 decimal places during validation.
 * It uses errors generated in the validating stage to identify the rows that need to be rectified.
 */
public class DataRectification {
    private final Collection<SurveyValidationError> errors;
    private Collection<StagedRow> rows;


    public DataRectification(Collection<StagedRow> rows, Collection<SurveyValidationError> errors) {
        this.errors = errors;
        this.rows = rows;
    }

    public Collection<StagedRow> rectify() {
        // If new data need to be rectified by the system, please add new functions and run them here.
        rectifyValidatedLatLonInDiff();
        rectifyOthers();

        return rows;
    }

    private void rectifyValidatedLatLonInDiff() {
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
                resultRows
                        .stream()
                        .filter(r -> r.getId() != null && r.getId().equals(rowId))
                        .findFirst()
                        .ifPresent(row ->
                                row.setLongitude(String.valueOf(
                                        Precision.round(
                                                Double.parseDouble(row.getLongitude()),
                                                COORDINATE_VALID_DECIMAL_COUNT
                                        ))));
                continue;
            }
            if (error.getMessage().contains("Latitude will be rounded to 5 decimal places")) {
                var rowId = error.getRowIds().iterator().next();
                resultRows
                        .stream()
                        .filter(r -> r.getId() != null && r.getId().equals(rowId))
                        .findFirst()
                        .ifPresent(row ->
                                row.setLatitude(String.valueOf(
                                        Precision.round(
                                                Double.parseDouble(row.getLatitude()), COORDINATE_VALID_DECIMAL_COUNT
                                        ))));
            }
        }
        rows = resultRows;
    }

    /**
     * This method is used to show the pattern of how to use new rectification methods in this class.
     */
    private void rectifyOthers() {}
}
