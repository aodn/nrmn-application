package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import lombok.val;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MeasureBetweenL5l95 extends BaseFormattedValidator {
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
        if (target.getMeasureJson().isEmpty() || l5 == null || l95 == null)
            return Validated.valid("No data");

        val outOfRange = target.getMeasureJson()
                .entrySet().stream()
                .filter(entry -> entry.getValue() != 0 && (entry.getValue() < l5 || entry.getValue() > l95))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        if (outOfRange.isEmpty()) {
            return Validated.valid("Measure in l5/l95 range");
        }

        return outOfRange.stream().map(measure -> {
            this.columnTarget = "Measure:" + measure;
            return invalid(
                    target,
                    "Measure: " + measure + " is outside l5/95[" + l5 + "," + l95 + "]",
                    ValidationCategory.DATA,
                    ValidationLevel.WARNING);
        }).reduce(Validated.valid(""), (acc, err) -> acc.combine(Monoids.stringConcat, err));

    }
}
