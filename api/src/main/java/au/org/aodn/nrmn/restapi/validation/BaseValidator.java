package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

public abstract class BaseValidator {


    protected String columnTarget;

    public BaseValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public Validated<StagedRowError, String> valid(StagedRow target);

}
