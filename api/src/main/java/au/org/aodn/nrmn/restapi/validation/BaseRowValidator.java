package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import cyclops.control.Validated;

public abstract class BaseRowValidator {


    protected String columnTarget;

    public BaseRowValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public <T> Validated<StagedRowError, T> valid(StagedRow target);

    public <T> Validated<StagedRowError, T> getError(StagedRow target, String message,
                                                     ValidationCategory category, ValidationLevel level) {
        return Validated.invalid(
                new StagedRowError(
                        new ErrorID(
                                target.getId(),
                                target.getStagedJob().getId(),
                                message),
                        category,
                        level,
                        columnTarget,
                        target));
    }

}
