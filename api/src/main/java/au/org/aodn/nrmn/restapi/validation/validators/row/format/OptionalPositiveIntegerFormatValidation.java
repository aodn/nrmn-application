package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowFormatValidation;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class OptionalPositiveIntegerFormatValidation extends BaseRowFormatValidation<Integer> {

    private final Function<StagedRow, String> getField;

    public OptionalPositiveIntegerFormatValidation(Function<StagedRow, String> getField, String columnTarget) {
        super(columnTarget, "Optional positive integer");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Optional<Integer>> valid(StagedRow target) {
        val fieldValue = getField.apply(target);
        if (StringUtils.isBlank(fieldValue)) {
            return Validated.valid(Optional.empty());
        }

        val integerValidation = new IntegerFormatValidation(getField, columnTarget, Collections.emptyList());
        val integerValidated = integerValidation.valid(target);

        if (integerValidated.isValid() && Integer.parseInt(fieldValue) < 0)
            return Validated.invalid(getError(target, "Must be positive"));

        return integerValidated.map(integer -> Optional.of(integer));
    }
}
