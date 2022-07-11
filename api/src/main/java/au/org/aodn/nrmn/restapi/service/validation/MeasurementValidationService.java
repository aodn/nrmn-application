package au.org.aodn.nrmn.restapi.service.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@Service
public class MeasurementValidationService {

    private static final double[] FISH_VALUES = { 2.5, 5, 7.5, 10, 12.5, 15, 20, 25, 30, 35, 40, 50, 62.5, 75, 87.5, 100, 112.5, 125, 137.5, 150, 162.5, 175, 187.5, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000 };
    private static final double[] INVERT_VALUES = { 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30 };

    // VALIDATION: Species size within L5 - L95
    private Collection<ValidationCell> validateMeasureRange(Long rowId, Boolean isInvertSized, Map<Integer, Integer> measurements, UiSpeciesAttributes speciesAttributes) {

        Collection<ValidationCell> errors = new ArrayList<ValidationCell>();

        double[] range = isInvertSized ? INVERT_VALUES : FISH_VALUES;

        Double l5 = speciesAttributes.getL5() != null ? speciesAttributes.getL5() : 0;
        Double l95 = speciesAttributes.getL95() != null ? speciesAttributes.getL95() : 0;

        if (l5 != 0 && l95 != 0) {
            var outOfRange = measurements.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() != 0 && (l5 > 0 && range[entry.getKey() - 1] < l5) || (l95 > 0 && range[entry.getKey() - 1] > l95))
                    .map(Map.Entry::getKey).collect(Collectors.toList());

            if (!outOfRange.isEmpty()) {
                String message = (isInvertSized ? "Invert measurements" : "Measurements") + " outside L5/95 [" + l5 + "," + l95 + "]";
                outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.BLOCKING, message, rowId, col.toString())));
            }
        }
        return errors;
    }

    public Collection<ValidationCell> validate(UiSpeciesAttributes speciesAttributes, StagedRowFormatted row) {
        
        boolean isMeasureMethod = !Arrays.asList(3, 4, 5).contains(row.getMethod());
        if (isMeasureMethod && row.getMeasureJson().size() > 0) {
            return validateMeasureRange(row.getId(), row.getIsInvertSizing(), row.getMeasureJson(), speciesAttributes);
        }
        return new ArrayList<ValidationCell>();
    }
}
