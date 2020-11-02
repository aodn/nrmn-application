package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
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

    protected Validated<StagedRowError, String> validFormat(
            Function<StagedRow, String> entry,
            ConsumerThrowable<String, Exception> formatCheck,
            StagedRow target
    ) {
        return Try.withCatch(() -> {
                    formatCheck.apply(entry.apply(target));
                    return Validated.<StagedRowError, String>valid(this.columnTarget + "is valid");
                }
        ).orElseGet(() ->
                Validated.invalid(new StagedRowError(
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
