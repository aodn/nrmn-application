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

    static final double[] FISH_VALUES = {2.5,5,7.5,10,12.5,15,20,25,30,35,40,50,62.5,75,87.5,100,112.5,125,137.5,150,162.5,175,187.5,200,250,300,350,400,450,500,550,600,650,700,750,800,850,900,950,1000};
    static final double[] INVERT_VALUES = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6, 6.5, 7, 7.5, 8, 8.5, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 14.5, 15, 16, 17, 18, 19, 20, 22, 24, 26, 28, 30};

    public MeasureBetweenL5l95() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val methodAllowed = Arrays.asList(0, 1, 2);
        if (!methodAllowed.contains(target.getMethod()) ||
                !target.getRef().getStagedJob().getIsExtendedSize()) {
            return Validated.valid("not affected");
        }

        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes = target.getSpeciesAttributesOpt().get();
        val l5 = speciesAttributes.getL5();
        val l95 = speciesAttributes.getL95();
        val measureJson = target.getMeasureJson();
        if (measureJson.isEmpty() || l5 == null || l95 == null)
            return Validated.valid("No data");

        boolean isInvertSized = (target.getIsInvertSizing().isPresent() && target.getIsInvertSizing().get() == true);

        // |measureJson| now contains the count of each species for a size column
        // Map this value to the size class and check the matching class is within l5 and l95
        val outOfRange = measureJson.entrySet().stream()
                .filter(entry -> entry.getValue() != 0 && (
                    ((isInvertSized) && (INVERT_VALUES[entry.getKey()-1] < l5 || INVERT_VALUES[entry.getKey()-1] > l95)) 
                 || ((!isInvertSized) && (FISH_VALUES[entry.getKey()-1] < l5 || FISH_VALUES[entry.getKey()-1] > l95))))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        if (outOfRange.isEmpty()) {
            return Validated.valid("Measure in l5/l95 range");
        }

        return outOfRange.stream().map(measure -> {
            this.columnTarget = "Measure:" + measure;
            val column = MeasureUtil.getMeasureName(measure, isInvertSized);
            return invalid(
                    target,
                    "Measure: " + column.replace('-', '.') + " is outside l5/95[" + l5 + "," + l95 + "]",
                    ValidationCategory.DATA,
                    ValidationLevel.WARNING, Optional.of(column));
        }).reduce(Validated.valid(""), (acc, err) -> acc.combine(Monoids.stringConcat, err));

    }
}
