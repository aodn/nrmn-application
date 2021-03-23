package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

public class TotalCheckSum extends BaseFormattedValidator {
    public TotalCheckSum() {
        super("Total");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val checkSum = target.getMeasureJson().entrySet()
                .stream().map(entry -> entry.getValue())
                .reduce(0,(acc, measure) -> acc + measure);
q
        if (target.getTotal() == checkSum) {
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
