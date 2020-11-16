package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

import java.util.*;

import java.util.function.Function;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormat extends BaseRowValidationFormat<Integer> {
    private Function<StagedRow, String> getField;
    private List<Integer> category;

    public IntegerFormat(Function<StagedRow, String> getField, String columnTarget, List<Integer> category) {
        super(columnTarget, "Numerical");
        this.getField = getField;
        this.category = category;
    }

    @Override
    public Validated<StagedRowError, Integer> valid(StagedRow target) {
        return validFormat(getField, (input) -> {
            Integer value = Integer.parseInt(input);
            if (!category.isEmpty() && !category.contains(value))
                return Validated.invalid(value + " must belong to " +
                        category.stream().map(Object::toString).reduce("", (e1, e2) -> e1 + " ," + e2)
                );
            return Validated.valid(value);

        }, target);
    }
}
