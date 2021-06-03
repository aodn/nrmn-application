package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowFormatValidation;
import cyclops.control.Validated;

import java.util.function.Function;

public class BooleanFormatValidation extends BaseRowFormatValidation<Boolean> {
    protected Function<StagedRow, String> getField;

    public BooleanFormatValidation(Function<StagedRow, String> getField, String columnTarget) {
        super(columnTarget, "Boolean");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Boolean> valid(StagedRow target) {
        return validFormat(getField, (input) ->  {
            if(input != null && (input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("NO")))
               return Validated.valid(input.equalsIgnoreCase("YES"));
            else
                return Validated.invalid("Must be 'Yes' or 'No'");
        }, target);
    }
}
