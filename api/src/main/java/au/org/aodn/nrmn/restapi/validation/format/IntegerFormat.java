package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import cyclops.control.Validated;

import java.util.function.Function;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormat extends BaseValidationFormat {
    private Function<StagedSurveyEntity, String> getField;

    IntegerFormat(Function<StagedSurveyEntity, String> getField, String colunmTarget) {
        this.format = "Numerical";
        this.columnTarget = colunmTarget;
        this.getField = getField;
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        return validFormat(getField, Integer::parseInt, target);
    }
}
