package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Validated;

import java.util.function.Function;

public class DoubleFormat extends BaseValidationFormat {
    protected Function<StagedSurvey, String> getField;

    DoubleFormat(Function<StagedSurvey, String> getField, String colunmTarget) {
        this.format = "Numerical";
        this.getField = getField;
        this.columnTarget = colunmTarget;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(getField, Double::parseDouble, target);
    }
}
