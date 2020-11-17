package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import cyclops.control.Validated;

import java.util.function.Function;

public class DiverExists extends BaseRowExistingEntity<Diver, DiverRepository> {
    Function<StagedRow, String> getField;

    public DiverExists(Function<StagedRow, String> getField, String columnTarget, DiverRepository diverRepo) {
        super(columnTarget, diverRepo);
        this.getField = getField;
    }

    @Override
    public Validated<StagedRowError, Diver> valid(StagedRow target) {
        return checkExists(target, getField.apply(target));
    }
}
