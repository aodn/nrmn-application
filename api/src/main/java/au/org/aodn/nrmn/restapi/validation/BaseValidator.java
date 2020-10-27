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
    protected ErrorCheckRepository errorRepo;


    abstract public Validated<ErrorCheck, String> valid(StagedSurvey target);


    protected <T> Validated<ErrorCheck, String> warningNotFound(List<T> entitiesFound, StagedSurvey target, String msg) {
        if (entitiesFound.isEmpty()) {
            val error = new ErrorCheck(
                new ErrorID(target.getId(),
                    target.getStagedJob().getId(),
                    msg + " Couldn't be found"),
                ValidationLevelType.WARNING,
                    columnTarget,
                target);
            val persistedError = errorRepo.save(error);
            return Validated.invalid(persistedError);
        }
        return Validated.valid(msg + " was found!");
    }
}
