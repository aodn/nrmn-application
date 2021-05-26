package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowFormatValidation;
import cyclops.control.Validated;

import java.util.function.Function;

public class BooleanFormatValidation extends BaseRowFormatValidation<Boolean> {
    protected Function<StagedRow, String> getField;

    public BooleanFormatValidation(Function<StagedRow, String> getField, String colunmTarget) {
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
