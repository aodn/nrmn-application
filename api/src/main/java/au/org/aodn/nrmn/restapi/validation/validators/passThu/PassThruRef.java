package au.org.aodn.nrmn.restapi.validation.validators.passThu;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;

public class PassThruRef extends BaseRowValidator {
    public PassThruRef() {
        super("ref");
    }

    @Override
    public Validated<StagedRowError, StagedRow> valid(StagedRow target) {
        return Validated.valid(target);
    }
}
