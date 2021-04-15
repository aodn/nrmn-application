package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import cyclops.control.Validated;

public abstract class BaseFormattedValidator {

    protected String columnTarget;

    public BaseFormattedValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public Validated<StagedRowError, String> valid(StagedRowFormatted target);

    public Validated<StagedRowError, String> invalid(StagedRowFormatted formattedRow, String errorMsg,
                                   ValidationCategory category, ValidationLevel level) {
        return Validated.invalid(new StagedRowError(
                new ErrorID(
                        formattedRow.getRef().getId(),
                        formattedRow.getRef().getStagedJob().getId(),
                        errorMsg
                ),
                ValidationCategory.DATA,
                ValidationLevel.BLOCKING,
                columnTarget,
               formattedRow.getRef()));
    }
}
