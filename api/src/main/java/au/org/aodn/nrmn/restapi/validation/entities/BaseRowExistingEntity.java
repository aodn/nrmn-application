package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public abstract class BaseRowExistingEntity< T extends CrudRepository>  extends BaseRowValidator {

    public BaseRowExistingEntity(String columnTarget) {
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
            return Validated.invalid(new ErrorCheck(errorID, ValidationCategory.ENTITY, columnTarget, target));

        return Validated.valid(fieldValue + " was found!");
    }
}
