package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

import java.util.function.Function;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormat extends BaseValidationFormat {
    private Function<StagedRow, String> getField;

    IntegerFormat(Function<StagedRow, String> getField, String colunmTarget) {
        super(colunmTarget, "Numerical");
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return validFormat(getField, Integer::parseInt, target);
    }
}
