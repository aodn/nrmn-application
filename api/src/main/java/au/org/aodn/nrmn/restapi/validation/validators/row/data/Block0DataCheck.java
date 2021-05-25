package au.org.aodn.nrmn.restapi.validation.validators.row.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseRowValidator;
import cyclops.control.Validated;
import lombok.val;

import java.util.Arrays;

public class Block0DataCheck extends BaseRowValidator {
    public Block0DataCheck() {
        super("Block");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        val block = Integer.parseInt(target.getBlock());
        val method = Integer.parseInt(target.getMethod());
        if (block != 0)
            return Validated.valid("block0 is valid");

        if (!Arrays.asList(0, 3, 4, 5).contains(method))
            return Validated.invalid(new StagedRowError(
                    new ErrorID(target.getId(),
                            target.getStagedJob().getId(),
                            "Block 0 must happen with M0, M3, M4, M5"),
                    ValidationCategory.DATA,
                    ValidationLevel.BLOCKING,
                    columnTarget,
                    target
            ));
        return Validated.valid("block0 is valid");

    }
}
