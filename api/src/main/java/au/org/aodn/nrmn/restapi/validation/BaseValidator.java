package au.org.aodn.nrmn.restapi.validation;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.ErrorCheckEntityRepository;
import cyclops.control.*;
import lombok.val;

import java.util.List;

public abstract class BaseValidator {

    protected String colunmTagert;
    protected ErrorCheckEntityRepository errorRepo;

    public BaseValidator(ErrorCheckEntityRepository errorRepo) {
        this.errorRepo = errorRepo;
    }

    abstract public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target);

    protected <T> Validated<ErrorCheckEntity, String> warningNotFound(List<T> entitiesFound, StagedSurveyEntity target, String msg) {
        if (entitiesFound.isEmpty()) {
            val error = new ErrorCheckEntity(
                    new ErrorID(target.rid.id, msg + " Couldn't be found", target.rid.fileID),
                    ValidationLevelType.WARNING,
                    colunmTagert,
                    target);
            val persistedError = errorRepo.save(error);
            return Validated.invalid(persistedError);
        }
        return Validated.valid(msg + " was found!");
    }
}
