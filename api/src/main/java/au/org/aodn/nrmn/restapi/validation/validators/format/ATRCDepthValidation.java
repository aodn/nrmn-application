package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;
import cyclops.data.tuple.Tuple2;

import java.util.regex.Pattern;

import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory.FORMAT;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.BLOCKING;


public class ATRCDepthValidation extends BaseRowValidator {

    private static final Pattern VALID_DEPTH = Pattern.compile("^[0-9]+$");
    private static final Pattern VALID_SURVEY_NUM = Pattern.compile("^[1-4]$");

    public ATRCDepthValidation() {
        super("Depth");
    }

    @Override
    public Validated<StagedRowError, Tuple2<Integer, Integer>> valid(StagedRow target) {
        String value = target.getDepth();
        String[] split = value.split("\\.");
        if (split.length != 2) {
            return getError(target, "Depth is invalid, expected: {depth}.{surveyNum}", FORMAT,
                    BLOCKING);
        }
        String depth = split[0];
        if (!VALID_DEPTH.matcher(depth).matches()) {
            return getError(target, "Depth is invalid, expected: Positive Integer for depth", FORMAT,
                    BLOCKING);
        }
        String surveyNum = split[1];
        if (!VALID_SURVEY_NUM.matcher(surveyNum).matches()) {
            return getError(target, "Depth is invalid, expected: 1, 2, 3 or 4 for surveyNum",
                    FORMAT, BLOCKING);
        }
        return Validated.valid(new Tuple2<>(Integer.parseInt(depth), Integer.parseInt(surveyNum)));
    }

}
