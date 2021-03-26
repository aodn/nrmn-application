package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class OptionalIntegerFormatValidation extends BaseRowFormatValidation<Integer> {

    private final Function<StagedRow, String> getField;

    public OptionalIntegerFormatValidation(Function<StagedRow, String> getField, String columnTarget) {
        super(columnTarget, "Optional integer");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Optional<Integer>> valid(StagedRow target) {
        if (StringUtils.isBlank(getField.apply(target))) {
            return Validated.valid(Optional.empty());
        }

        val integerValidation = new IntegerFormatValidation(getField, columnTarget, Collections.emptyList());
        val integerValidated = integerValidation.valid(target);

        return integerValidated.bimap(Function.identity(), integer -> Optional.of(integer));
    }
}
