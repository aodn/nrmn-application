package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory.FORMAT;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.BLOCKING;

import java.util.regex.Pattern;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import cyclops.control.Validated;


public class RLSDepthValidation extends BaseRowValidator {

    private static final Pattern VALID_DEPTH_SURVEY_NUM = Pattern.compile("^[0-9]+(\\.[0-9])?$");

    public RLSDepthValidation() {
        super("depth");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        String value = target.getDepth();
        if (value == null || !VALID_DEPTH_SURVEY_NUM.matcher(value).matches()) {
            return getError(target, "Depth is invalid, expected: depth[.surveyNum]", FORMAT, BLOCKING);
        }
        return Validated.valid(value);
    }
}
