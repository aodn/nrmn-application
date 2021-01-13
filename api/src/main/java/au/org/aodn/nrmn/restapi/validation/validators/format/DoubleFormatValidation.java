package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;
import lombok.val;

import java.util.function.Function;

public class DoubleFormatValidation extends BaseRowFormatValidation<Double> {
    protected Function<StagedRow, String> getField;

    public DoubleFormatValidation(Function<StagedRow, String> getField, String colunmTarget) {
        super(colunmTarget, "Numerical");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Double> valid(StagedRow target) {
        return validFormat(getField, (input) -> {
            val doubleValue = Double.parseDouble(input);
            return Validated.valid(doubleValue);
        }, target);
    }
}
