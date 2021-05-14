package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public class OptionalBooleanFormatValidation extends BaseRowFormatValidation<Optional<Boolean>> {

    private final Function<StagedRow, String> getField;

    public OptionalBooleanFormatValidation(Function<StagedRow, String> getField, String columnTarget) {
        super(columnTarget, "Optional boolean");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Optional<Boolean>> valid(StagedRow target) {
        if (StringUtils.isBlank(getField.apply(target))) {
            return Validated.valid(Optional.empty());
        }

        val booleanValidation = new BooleanFormatValidation(getField, columnTarget);
        val booleanValidated = booleanValidation.valid(target);

        return booleanValidated.bimap(Function.identity(), value -> Optional.of(value));
    }
}
