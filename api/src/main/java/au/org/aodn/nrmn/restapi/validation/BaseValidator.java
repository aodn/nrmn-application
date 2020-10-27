package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import cyclops.control.*;
import lombok.val;

import java.util.List;

public abstract class BaseValidator {

    public String columnTarget;

    abstract public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target);


}
