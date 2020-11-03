package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Validated;

import java.util.function.Function;

public class DoubleFormat extends BaseRowValidationFormat {
    protected Function<StagedSurvey, String> getField;

    DoubleFormat(Function<StagedSurvey, String> getField, String colunmTarget) {
        super(colunmTarget, "Numerical");
        this.getField = getField;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(getField, (input) -> Validated.valid(Double.parseDouble(input)), target);
    }
}
