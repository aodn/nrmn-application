package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.ValidationLevelType;
import au.org.aodn.nrmn.restapi.validation.format.BaseValidationFormat;
import cyclops.control.Validated;
import lombok.val;

import java.util.List;

public abstract class BaseExistingEntity extends BaseValidationFormat {

    protected <T> Validated<ErrorCheckEntity, String> warningNotFound(List<T> entitiesFound, StagedSurveyEntity target, String fieldValue) {
        val errorID = new ErrorID(
                target.getId(),
                target.getStagedJob().getId(),
                fieldValue + "couldn't be found"
        );

        if (fieldValue == null || fieldValue.isEmpty())
            errorID.setMessage(columnTarget + "is empty");

        if (entitiesFound.isEmpty())
            return Validated.invalid(new ErrorCheckEntity(errorID, ValidationLevelType.WARNING, columnTarget, target));

        return Validated.valid(fieldValue + " was found!");
    }
}
