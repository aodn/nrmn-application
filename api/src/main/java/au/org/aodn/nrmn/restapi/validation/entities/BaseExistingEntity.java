package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.BaseValidator;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import au.org.aodn.nrmn.restapi.validation.format.BaseValidationFormat;
import cyclops.control.Validated;
import lombok.val;

import java.util.List;

public abstract class BaseExistingEntity extends BaseValidator {


    public BaseExistingEntity(String columnTarget) {
        super(columnTarget);
    }

    protected <T> Validated<ErrorCheck, String> warningNotFound(List<T> entitiesFound, StagedSurvey target, String fieldValue) {
        val errorID = new ErrorID(
                target.getId(),
                target.getStagedJob().getId(),
                fieldValue + "couldn't be found"
        );

        if (fieldValue == null || fieldValue.isEmpty())
            errorID.setMessage(columnTarget + "is empty");

        if (entitiesFound.isEmpty())
            return Validated.invalid(new ErrorCheck(errorID, ValidationLevelType.WARNING, columnTarget, target));

        return Validated.valid(fieldValue + " was found!");
    }
}
