package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;

import java.util.function.Function;

public class BooleanFormat extends BaseRowValidationFormat {
    protected Function<StagedRow, String> getField;

    BooleanFormat(Function<StagedRow, String> getField, String colunmTarget) {
        super(colunmTarget, "Boolean");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return validFormat(getField, (input) ->  {
            Boolean value = Boolean.parseBoolean(input);
           return Validated.valid(value.toString());
        }, target);
    }
}
