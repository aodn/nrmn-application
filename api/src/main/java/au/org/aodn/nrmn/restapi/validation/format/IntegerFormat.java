package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Option;
import cyclops.control.Validated;

import java.util.*;
import java.util.function.Function;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormat extends BaseRowValidationFormat {
    private Function<StagedSurvey, String> getField;
    private List<Integer> category;

    IntegerFormat(Function<StagedSurvey, String> getField, String columnTarget, List<Integer> category) {
        super(columnTarget, "Numerical");
        this.getField = getField;
        this.category = category;
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(getField, (input) -> {
            Integer value = Integer.parseInt(input);
            if (!category.isEmpty() && !category.contains(value))
                return Validated.invalid(value + " must belong to " +
                        category.stream().map(Object::toString).reduce("", (e1, e2) -> e1 + " ," + e2)
                );
            return Validated.valid(value.toString());

        }, target);
    }
}
