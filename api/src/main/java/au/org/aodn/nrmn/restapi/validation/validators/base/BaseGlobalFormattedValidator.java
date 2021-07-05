package au.org.aodn.nrmn.restapi.validation.validators.base;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;

import java.util.List;

public abstract class BaseGlobalFormattedValidator {
    protected String ruleName;

    public BaseGlobalFormattedValidator(String ruleName){
        this.ruleName = ruleName;
    }

    abstract public Validated<StagedRowError, String> valid(StagedJob job, List<StagedRowFormatted> rows );

    protected Validated<StagedRowError, String> invalid(Long id, String message, ValidationLevel level, StagedRowFormatted targetRow) {
        return Validated.invalid(
                new StagedRowError(
                        new ErrorID(
                                targetRow != null ? targetRow.getRef().getId() : null,
                                id,
                                message),
                        ValidationCategory.GLOBAL,
                        level,
                        ruleName,
                        targetRow != null ? targetRow.getRef() : null
                ));
    }
}
