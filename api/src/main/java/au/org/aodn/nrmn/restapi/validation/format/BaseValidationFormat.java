package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.util.ConsumerThrowable;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import cyclops.control.Try;
import cyclops.control.Validated;

import java.util.function.Function;


public abstract class BaseValidationFormat extends BaseValidator {

    protected String format;

    protected Validated<ErrorCheckEntity, String> validFormat(
            Function<StagedSurveyEntity, String> entry,
            ConsumerThrowable<String, Exception> formatCheck,
            StagedSurveyEntity target
    ) {
        return Try.withCatch(() -> {
                    formatCheck.apply(entry.apply(target));
                    return Validated.<ErrorCheckEntity, String>valid(this.columnTarget + "is valid");
                }
        ).orElseGet(() ->
                Validated.invalid(new ErrorCheckEntity(
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
