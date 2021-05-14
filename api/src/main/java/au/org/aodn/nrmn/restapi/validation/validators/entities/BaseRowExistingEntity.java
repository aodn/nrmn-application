package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import cyclops.control.Validated;

public abstract class BaseRowExistingEntity<E, R extends EntityCriteria<E>> extends BaseRowValidator {

    protected R repo;

    public BaseRowExistingEntity(String columnTarget, R repo) {
        super(columnTarget);
        this.repo = repo;
    }

    protected Validated<StagedRowError, E> checkExists(StagedRow target, String criteria, ValidationLevel errorLevel) {
        if (criteria == null || criteria.isEmpty()) {
            return invalid(target, errorLevel, columnTarget + " is empty");
        }

        return repo.findByCriteria(criteria)
                .stream()
                .findFirst()
                .map(Validated::<StagedRowError, E>valid)
                .orElseGet(() -> invalid(target, errorLevel, criteria + " couldn't be found"));
    }

    private Validated<StagedRowError, E> invalid(StagedRow target, ValidationLevel errorLevel, String error) {
        return Validated.invalid(new StagedRowError(
                new ErrorID(
                        target.getId(),
                        target.getStagedJob().getId(),
                        error),
                ValidationCategory.ENTITY,
                errorLevel,
                columnTarget,
                target));
    }
}
