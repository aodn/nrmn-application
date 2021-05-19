package au.org.aodn.nrmn.restapi.validation.validators.base;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;

import java.util.Optional;

public abstract class BaseFormattedValidator {

    protected String columnTarget;

    public BaseFormattedValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public Validated<StagedRowError, String> valid(StagedRowFormatted target);

    public Validated<StagedRowError, String> invalid(StagedRowFormatted formattedRow, String errorMsg,
                                   ValidationCategory category, ValidationLevel level, Optional<String> target) {
        return Validated.invalid(new StagedRowError(
                new ErrorID(
                        formattedRow.getRef().getId(),
                        formattedRow.getRef().getStagedJob().getId(),
                        errorMsg
                ),
                category,
                level,
                target.orElseGet(() -> columnTarget),
               formattedRow.getRef()));
    }
}
