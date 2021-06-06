package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.util.MeasureUtil;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import lombok.val;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MeasureBetweenL5l95 extends BaseFormattedValidator {

    public MeasureBetweenL5l95() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {

        val methodAllowed = Arrays.asList(3,4,5);
        if (methodAllowed.contains(target.getMethod())) {
            return Validated.valid("M3, M4, M5 species");
        }

        if(!target.getRef().getStagedJob().getIsExtendedSize()) {
            return Validated.valid("Not extended sizing");
        }

        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes = target.getSpeciesAttributesOpt().get();
        val l5 = speciesAttributes.getL5() != null ? speciesAttributes.getL5() : 0;
        val l95 = speciesAttributes.getL95() != null ? speciesAttributes.getL95() : 0;
        val measureJson = target.getMeasureJson();
        if (measureJson.isEmpty() || (l5 == 0 && l95 == 0))
            return Validated.valid("No expected sizing");

        boolean isInvertSized = target.getIsInvertSizing();

        // |measureJson| now contains the count of each species for a size column
        // Map this value to the size class and check the matching class 
        // is within l5 and l95
        val outOfRange = measureJson.entrySet().stream()
                .filter(entry -> entry.getValue() != 0 && (((isInvertSized)
                        && ((l5 > 0 && INVERT_VALUES[entry.getKey() - 1] < l5) || (l95 > 0 && INVERT_VALUES[entry.getKey() - 1] > l95)))
                        || ((!isInvertSized)
                                && ((l5 > 0 && FISH_VALUES[entry.getKey() - 1] < l5) || (l95 > 0 && FISH_VALUES[entry.getKey() - 1] > l95)))))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        if (outOfRange.isEmpty()) {
            return Validated.valid("Measure in l5/l95 range");
        }

        return outOfRange.stream().map(measure -> {
            this.columnTarget = "Measure:" + measure;
            val column = MeasureUtil.getMeasureName(measure, isInvertSized);
            return invalid(target, "Measure: " + column.replace('-', '.') + " is outside l5/95[" + l5 + "," + l95 + "]",
                    ValidationCategory.DATA, ValidationLevel.WARNING, Optional.of(column));
        }).reduce(Validated.valid(""), (acc, err) -> acc.combine(Monoids.stringConcat, err));

    }
}
