package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.util.ConsumerThrowable;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import cyclops.control.Try;
import cyclops.control.Validated;

import java.util.function.Function;


public abstract class BaseValidationFormat extends BaseValidator {

    protected String format;


    public BaseValidationFormat(String columnTarget, String format) {
        super(columnTarget);
        this.format = format;
    }

    protected Validated<ErrorCheck, String> validFormat(
            Function<StagedSurvey, String> entry,
            ConsumerThrowable<String, Exception> formatCheck,
            StagedSurvey target
    ) {
        return Try.withCatch(() -> {
                    formatCheck.apply(entry.apply(target));
                    return Validated.<ErrorCheck, String>valid(this.columnTarget + "is valid");
                }
        ).orElseGet(() ->
                Validated.invalid(new ErrorCheck(
                        new ErrorID(target.getId(),
                                target.getStagedJob().getId(),
                                this.columnTarget + " format is invalid," + "expected format: " + this.format),
                        ValidationLevelType.WARNING,
                        columnTarget,
                        target)
                )
        );
    }
}
