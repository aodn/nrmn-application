package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;

import java.util.function.Function;

public class BooleanFormat extends BaseRowValidationFormat<Boolean> {
    protected Function<StagedRow, String> getField;

    public BooleanFormat(Function<StagedRow, String> getField, String colunmTarget) {
        super(colunmTarget, "Boolean");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Boolean> valid(StagedRow target) {
        return validFormat(getField, (input) ->  {
            Boolean value = Boolean.parseBoolean(input);
           return Validated.valid(value);
        }, target);
    }
}
