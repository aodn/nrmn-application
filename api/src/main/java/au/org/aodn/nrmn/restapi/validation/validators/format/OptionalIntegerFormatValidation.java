package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.function.Function;

public class OptionalIntegerFormatValidation extends IntegerFormatValidation {

    private final Function<StagedRow, String> getField;

    public OptionalIntegerFormatValidation(Function<StagedRow, String> getField, String columnTarget) {
        super(getField, columnTarget, Collections.emptyList());
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Integer> valid(StagedRow target) {
        if (StringUtils.isBlank(getField.apply(target))) {
            return Validated.valid(null);
        }

        return super.valid(target);
    }
}
