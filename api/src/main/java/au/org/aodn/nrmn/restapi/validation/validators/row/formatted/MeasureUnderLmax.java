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
import java.util.Optional;
import java.util.stream.Collectors;

public class MeasureUnderLmax extends BaseFormattedValidator {
    public MeasureUnderLmax() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {

        val skipMethods = Arrays.asList(3,4,5);
        if (skipMethods.contains(target.getMethod())) {
            return Validated.valid("M3, M4, M5 species");
        }
        
        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes = target.getSpeciesAttributesOpt().get();
        val lmax = speciesAttributes.getLmax() != null ? speciesAttributes.getLmax() : 0;
        if (target.getMeasureJson().isEmpty() || lmax == 0)
            return Validated.valid("No expected sizing");

        // Use isInvertSizing column value only if extended sizing is set
        boolean isInvertSized = target.getRef().getStagedJob().getIsExtendedSize() && target.getIsInvertSizing() != null ? 
                                target.getIsInvertSizing() : 
                                false;

        val outOfRangef = target.getMeasureJson().entrySet().stream()
                .filter(entry -> isInvertSized ? INVERT_VALUES[entry.getKey() - 1] > lmax
                        : FISH_VALUES[entry.getKey() - 1] > lmax)
                .collect(Collectors.toList());

        if (outOfRangef.isEmpty()) {
            return Validated.valid("Values under Lmax");
        }
        return outOfRangef.stream().map(measure -> {
            val column = MeasureUtil.getMeasureName(measure.getKey(), isInvertSized);
            return invalid(target, "Measure [" + column.replace('-', '.') + "] is above Lmax [" + lmax + "] for Species [" + speciesAttributes.getSpeciesName() + "]",
                    ValidationCategory.DATA, ValidationLevel.WARNING, Optional.of(column));
        }).reduce(Validated.valid(""), (acc, err) -> acc.combine(Monoids.stringConcat, err));
    }
}
