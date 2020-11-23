package au.org.aodn.nrmn.restapi.validation.validators.passThu;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.validators.format.BaseRowFormatValidation;
import cyclops.control.Validated;

import java.util.function.Function;

public class PassThruString extends BaseRowFormatValidation<String> {

    private Function<StagedRow, String> entry;

    public PassThruString(Function<StagedRow, String> entry,
                          String columnTarget) {
        super(columnTarget, "");
        this.entry = entry;
    }

    @Override
    public  Validated<StagedRowError, String> valid(StagedRow target) {
        return validFormat(entry, Validated::valid,target);
    }
}
