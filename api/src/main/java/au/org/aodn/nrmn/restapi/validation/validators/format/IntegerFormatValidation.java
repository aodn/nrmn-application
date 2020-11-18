package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

import java.util.*;

import java.util.function.Function;
import java.util.stream.Collectors;
/*
 * Example:  new IntegerFormat(){ this.fieldValue = target.getL5(); this.colunmTarget = "L5";}
 *
 */

public class IntegerFormatValidation extends BaseRowFormatValidation<Integer> {
    private Function<StagedRow, String> getField;
    private List<Integer> validValues;

    public IntegerFormatValidation(Function<StagedRow, String> getField, String columnTarget, List<Integer> validValues) {
        super(columnTarget, "Numerical");
        this.getField = getField;
        this.validValues = validValues;
    }

    @Override
    public Validated<StagedRowError, Integer> valid(StagedRow target) {
        return validFormat(getField, (input) -> {
            Integer value = Integer.parseInt(input);
            if (!validValues.isEmpty() && !validValues.contains(value))
                return Validated.invalid(value + " must belong to " +
                        validValues.stream().map(Object::toString).collect(Collectors.joining()));
            return Validated.valid(value);

        }, target);
    }
}
