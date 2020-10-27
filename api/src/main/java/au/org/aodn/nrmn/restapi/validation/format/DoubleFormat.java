package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import cyclops.control.Validated;

import java.util.function.Function;

public class DoubleFormat extends BaseValidationFormat {
    protected Function<StagedSurveyEntity, String> getField;

    DoubleFormat(Function<StagedSurveyEntity, String> getField, String colunmTarget) {
        this.format = "Numerical";
        this.getField = getField;
        this.columnTarget = colunmTarget;
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        return validFormat(getField, Double::parseDouble, target);
    }
}
