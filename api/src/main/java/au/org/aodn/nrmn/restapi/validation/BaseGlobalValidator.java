package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import cyclops.control.Validated;

public abstract class BaseGlobalValidator {


    protected String ruleName;

    public BaseGlobalValidator(String ruleName){
        this.ruleName = ruleName;
    }

    abstract public Validated<StagedRowError, String> valid(StagedJob job);

    protected Validated<StagedRowError, String> invalid(Long id, String message, ValidationLevel level) {
        return Validated.invalid(
                new StagedRowError(
                        new ErrorID(
                                null,
                                id,
                                message),
                        ValidationCategory.GLOBAL,
                        level,
                        ruleName,
                        null
                ));
    }
}
