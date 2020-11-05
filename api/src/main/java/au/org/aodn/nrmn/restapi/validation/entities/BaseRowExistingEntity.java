package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.control.Validated;
import lombok.val;

import java.util.List;

public abstract class BaseRowExistingEntity extends BaseRowValidator {

    public BaseRowExistingEntity(String columnTarget) {
        super(columnTarget);
    }

    protected <T> Validated<StagedRowError, String> warningNotFound(List<T> entitiesFound, StagedRow target, String fieldValue) {
        val errorID = new ErrorID(
                target.getId(),
                target.getStagedJob().getId(),
                fieldValue + "couldn't be found"
        );

        if (fieldValue == null || fieldValue.isEmpty())
            errorID.setMessage(columnTarget + "is empty");

        if (entitiesFound.isEmpty())
            return Validated.invalid(new StagedRowError(errorID, ValidationCategory.ENTITY, columnTarget, target));

        return Validated.valid(fieldValue + " was found!");
    }
}
