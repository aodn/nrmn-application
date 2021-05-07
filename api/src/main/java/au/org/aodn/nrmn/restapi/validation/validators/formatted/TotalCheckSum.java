package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.Map;

public class TotalCheckSum extends BaseFormattedValidator {
    public TotalCheckSum() {
        super("Total");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val checkSum = target.getMeasureJson().entrySet()
                .stream().map(Map.Entry::getValue)
                .reduce(0, Integer::sum);

        if (target.getTotal().equals(checkSum)) {
            return Validated.valid("Checksum match Total");
        }
        return Validated.invalid(new StagedRowError(
                new ErrorID(target.getId(),
                        target.getRef().getStagedJob().getId(),
                        "CheckSum didn't match Total"),
                ValidationCategory.DATA,
                ValidationLevel.WARNING,
                columnTarget,
                target.getRef()));
    }
}
