package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;

import java.util.Optional;

import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory.DATA;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.BLOCKING;

public class ATRCMethod7BlockCheck extends BaseRowValidator {

    public ATRCMethod7BlockCheck() {
        super("Block");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        Optional<String> method = Optional.ofNullable(target.getMethod());
        Optional<String> block = Optional.ofNullable(target.getBlock());

        if (!method.isPresent()
                || !method.get().equals("7")
                || (block.isPresent() && block.get().equals("2"))) {
            return Validated.valid("block is valid");
        } else {
            return getError(target, "Method 7 must be recorded on block 2", DATA, BLOCKING);
        }
    }
}
