package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.util.ConsumerThrowable;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.control.Try;
import cyclops.control.Validated;

import java.util.function.Function;


public abstract class BaseRowValidationFormat extends BaseRowValidator {

    protected String format;


    public BaseRowValidationFormat(String columnTarget, String format) {
        super(columnTarget);
        this.format = format;
    }

    protected ErrorCheck getError(StagedSurvey target, String msg) {
        return new ErrorCheck(
                new ErrorID(target.getId(),
                        target.getStagedJob().getId(), msg),
                ValidationCategory.FORMAT,
                columnTarget,
                target);
    }

    protected <T> Validated<ErrorCheck, String> validFormat(
            Function<StagedSurvey, String> entry,
            ConsumerThrowable<String, Validated<String, T>, Exception> formatCheck,
            StagedSurvey target
    ) {
        return Try.withCatch(() ->
                formatCheck
                        .apply(entry.apply(target))
                        .bimap(
                                errMsg -> getError(target, errMsg),
                                success -> columnTarget + " format is valid")
        ).orElseGet(() ->
                Validated.invalid(
                        getError(target,
                                this.columnTarget + " format is invalid," + "expected format: " + this.format)
                )
        );
    }
}
