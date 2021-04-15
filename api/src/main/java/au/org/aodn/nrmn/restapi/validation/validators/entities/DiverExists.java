package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import cyclops.control.Validated;

import java.util.function.Function;

public class DiverExists extends BaseRowExistingEntity<Diver, DiverRepository> {
    private Function<StagedRow, String> getField;
    private  ValidationLevel errorLevel;
    public DiverExists(Function<StagedRow, String> getField,
                       String columnTarget,
                       DiverRepository diverRepo,
                       ValidationLevel errorLevel) {
        super(columnTarget, diverRepo);
        this.getField = getField;
        this.errorLevel = errorLevel;
    }

    @Override
    public Validated<StagedRowError, Diver> valid(StagedRow target) {
        return checkExists(target, getField.apply(target), errorLevel);
    }
}
