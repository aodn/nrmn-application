package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;

public abstract class BaseGlobalValidator {


    protected String ruleName;

    public BaseGlobalValidator(String ruleName){
        this.ruleName = ruleName;
    }

    abstract public Validated<StagedRowError, String> valid(StagedJob job);

}
