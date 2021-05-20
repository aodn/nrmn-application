package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
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
            if (!validValues.isEmpty() && !validValues.contains(value)) {
                return Validated.invalid( "[" +value + "] is invalid. Must be " + format(validValues));
            }
            return Validated.valid(value);
        }, target);
    }

    private String format(List<Integer> values) {
        return values.size() == 1 ?
                values.get(0).toString() :
                new StringJoiner(" or ")
                        .add(StringUtils.join(values, ", ", 0, values.size() - 1))
                        .add(values.get(values.size() - 1).toString())
                        .toString();
    }
}
