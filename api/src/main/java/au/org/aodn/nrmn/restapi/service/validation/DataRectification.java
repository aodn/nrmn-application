package au.org.aodn.nrmn.restapi.service.validation;


import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import org.apache.commons.math3.util.Precision;

import java.util.Collection;

import static au.org.aodn.nrmn.restapi.util.Constants.*;

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
        for (var error: errors) {
            rectifyValidatedLatLonInDiff(error);
            rectifyOthers(error);
        }

        return rows;
    }

    private void rectifyValidatedLatLonInDiff(SurveyValidationError error) {
        if (error.getMessage().contains(NULLIFY_LAT_LON_MSG_SUFFIX)) {
            var rowId = error.getRowIds().iterator().next();
            var row = rows.stream().filter(r -> r.getId().equals(rowId)).findFirst().orElse(null);
            if (row != null) {
                row.setLatitude(null);
                row.setLongitude(null);
            }
            return;
        }
        if (error.getMessage().contains(ROUND_LON_MSG)) {
            var row = findRelatedRowOf(error);
            if (row != null) {
                row.setLongitude(String.valueOf(
                        Precision.round(
                                Double.parseDouble(row.getLongitude()), COORDINATE_VALID_DECIMAL_COUNT
                        )));
            }
            return;
        }
        if (error.getMessage().contains(ROUND_LAT_MSG)) {

                var row = findRelatedRowOf(error);
                if (row != null) {
                    row.setLatitude(String.valueOf(
                            Precision.round(
                                    Double.parseDouble(row.getLatitude()), COORDINATE_VALID_DECIMAL_COUNT
                            )));
                }
        }
    }

    private StagedRow findRelatedRowOf(SurveyValidationError error) {
        var rowId = error.getRowIds().iterator().next();
        return rows.stream().filter(r -> r.getId().equals(rowId)).findFirst().orElse(null);
    }

    /**
     * This method is used to show the pattern of how to use new rectification methods in this class.
     */
    private void rectifyOthers(SurveyValidationError ignored) {}
}
