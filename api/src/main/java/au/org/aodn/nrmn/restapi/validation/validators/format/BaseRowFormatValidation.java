package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.util.ConsumerThrowable;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.companion.Functions;
import cyclops.control.Try;
import cyclops.control.Validated;

import java.util.function.Function;


public abstract class BaseRowFormatValidation<T> extends BaseRowValidator {

    protected String format;


    public BaseRowFormatValidation(String columnTarget, String format) {
        super(columnTarget);
        this.format = format;
    }

    protected StagedRowError getError(StagedRow target, String msg) {
        return new StagedRowError(
                new ErrorID(target.getId(),
                        target.getStagedJob().getId(), msg),
                ValidationCategory.FORMAT,
                ValidationLevel.BLOCKING,
                columnTarget,
                target);
    }

    protected Validated<StagedRowError, T> validFormat(
            Function<StagedRow, String> entry,
            ConsumerThrowable<String, Validated<String, T>, Exception> formatCheck,
            StagedRow target
    ) {
        return Try.withCatch(() ->
                formatCheck
                        .apply(entry.apply(target).trim())
                        .<StagedRowError, T>bimap(
                                errMsg -> getError(target, errMsg),
                                Functions.identity())
        ).orElseGet(() ->
                Validated.invalid(
                        getError(target, "Column:" + this.columnTarget.replaceAll("-", ".") + ", "+entry.apply(target) + " is invalid, expected : " + this.format)
                )
        );
    }
}
