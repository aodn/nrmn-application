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

import java.util.Optional;
import java.util.stream.Collectors;

public class MeasureUnderLmax extends BaseFormattedValidator {
    public MeasureUnderLmax() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (!target.getRef().getStagedJob().getIsExtendedSize()) {
            return Validated.valid("not affected");
        }

        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes = target.getSpeciesAttributesOpt().get();
        val lmax = speciesAttributes.getLmax() != null ? speciesAttributes.getLmax() : 0;
        if (target.getMeasureJson().isEmpty() || lmax == 0)
            return Validated.valid("No expected sizing");

        val outOfRangef = target.getMeasureJson().entrySet().stream()
                .filter(entry -> target.getIsInvertSizing() ? INVERT_VALUES[entry.getKey() - 1] > lmax
                        : FISH_VALUES[entry.getKey() - 1] > lmax)
                .collect(Collectors.toList());

        if (outOfRangef.isEmpty()) {
            return Validated.valid("Values under Lmax");
        }
        return outOfRangef.stream().map(measure -> {
            val column = MeasureUtil.getMeasureName(measure.getKey(), target.getIsInvertSizing());
            return invalid(target, "Measure: " + column.replace('-', '.') + " is above Lmax[" + lmax + "]",
                    ValidationCategory.DATA, ValidationLevel.WARNING, Optional.of(column));
        }).reduce(Validated.valid(""), (acc, err) -> acc.combine(Monoids.stringConcat, err));
    }
}
