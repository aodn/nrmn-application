package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckRepository;
import cyclops.control.Validated;
import lombok.val;

import java.util.List;

public abstract class BaseValidator {


    protected String columnTarget;

    public BaseValidator(String columnTarget){
        this.columnTarget = columnTarget;
    }
    abstract public Validated<ErrorCheck, String> valid(StagedSurvey target);

}
