package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Validated;

import java.util.function.Function;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormat extends BaseValidationFormat {
    private Function<StagedSurvey, String> getField;

    IntegerFormat(Function<StagedSurvey, String> getField, String colunmTarget) {
        super(colunmTarget, "Numerical");
        this.getField = getField;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(getField, Integer::parseInt, target);
    }
}
