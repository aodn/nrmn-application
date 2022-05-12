package au.org.aodn.nrmn.restapi.service.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.validation.process.ValidationResultSet;

@Service
public class MeasurementValidationService {

    // results.addAll(validateMeasureRange(isExtended, row, speciesAttributes), false);
    // results.addAll(validateMeasureUnderMax(isExtended, row, speciesAttributes), false);
    // res.addAll(validateAbundance(row, speciesAttributes));

    // VALIDATION: Species size within L5 - L95
    // private Collection<ValidationCell> validateMeasureRange(Boolean isExtended, StagedRowFormatted row, UiSpeciesAttributes speciesAttributes) {

    //     Collection<ValidationCell> errors = new ArrayList<ValidationCell>();

    //     boolean isInvertSized = isExtended && row.getIsInvertSizing();
    //     double[] range = isInvertSized ? INVERT_VALUES : FISH_VALUES;

    //     Double l5 = speciesAttributes.getL5() != null ? speciesAttributes.getL5() : 0;
    //     Double l95 = speciesAttributes.getL95() != null ? speciesAttributes.getL95() : 0;

    //     if (l5 != 0 && l95 != 0) {
    //         List<Integer> outOfRange = row.getMeasureJson().entrySet()
    //                 .stream()
    //                 .filter(entry -> entry.getValue() != 0 && (l5 > 0 && range[entry.getKey() - 1] < l5) || (l95 > 0 && range[entry.getKey() - 1] > l95))
    //                 .map(Map.Entry::getKey).collect(Collectors.toList());

    //         if (!outOfRange.isEmpty()) {
    //             String message = (isInvertSized ? "Invert measurements" : "Measurements") + " outside L5/95 [" + l5 + "," + l95 + "] for [" + row.getRef().getSpecies() + "]";
    //             outOfRange.stream().forEach(col -> errors.add(new ValidationCell(ValidationCategory.DATA, ValidationLevel.INFO, message, row.getId(), col.toString())));
    //         }
    //     }
    //     return errors;
    //}

    public Collection<ValidationError> validate(Map<Integer, UiSpeciesAttributes> speciesAttributes,
            Integer observableItemId, Integer method, Map<Integer, String> measurements) {

        Collection<ValidationCell> errors = new ArrayList<ValidationCell>();
        Set<ValidationError> res = new HashSet<ValidationError>();
        ValidationResultSet results = new ValidationResultSet();

        if (!speciesAttributes.containsKey(observableItemId)) {
            // ObservableItem doesn't have attributes; fatal
            return null;
        }

        if (Arrays.asList(3, 4, 5).contains(method) || measurements.size() < 1)
            return null;

        return null;
    }
}
