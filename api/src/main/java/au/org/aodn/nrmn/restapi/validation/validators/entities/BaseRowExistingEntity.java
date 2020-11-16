package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import cyclops.control.Validated;
import lombok.val;

public abstract class BaseRowExistingEntity<E, R extends EntityCriteria<E>> extends BaseRowValidator {

    protected R repo;

    public BaseRowExistingEntity(String columnTarget, R repo) {
        super(columnTarget);
        this.repo = repo;
    }

    protected Validated<StagedRowError, E> warningNotFound(StagedRow target, String criteria) {
        val errorID = new ErrorID(
                target.getId(),
                target.getStagedJob().getId(),
                criteria + " couldn't be found"
        );

        if (criteria == null || criteria.isEmpty())
            errorID.setMessage(columnTarget + "is empty");

        return repo.findByCriteria(criteria)
                .map( Validated::<StagedRowError, E>valid)
                .orElseGet(() ->
             Validated.<StagedRowError, E>invalid(
                     new StagedRowError(
                             errorID,
                             ValidationCategory.ENTITY,
                             columnTarget,
                             target))
        );
    }
}
