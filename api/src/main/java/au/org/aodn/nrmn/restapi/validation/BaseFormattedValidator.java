package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;

public abstract class BaseFormattedValidator {

    protected String columnTarget;

    public BaseFormattedValidator(String columnTarget) {
        this.columnTarget = columnTarget;
    }

    abstract public <T> Validated<StagedRowError, T> valid(StagedRowFormatted target);
}
