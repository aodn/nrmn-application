package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

import java.util.function.Function;

public class DoubleFormat extends BaseRowValidationFormat<Double> {
    protected Function<StagedRow, String> getField;

    public  DoubleFormat(Function<StagedRow, String> getField, String colunmTarget) {
         super(colunmTarget, "Numerical");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Double> valid(StagedRow target) {
        return validFormat(getField, (input) ->
                Validated.valid(Double.parseDouble(input)), target);
    }
}
