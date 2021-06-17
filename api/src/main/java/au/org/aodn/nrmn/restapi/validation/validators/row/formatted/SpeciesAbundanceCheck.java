package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.Optional;

public class SpeciesAbundanceCheck extends BaseFormattedValidator {
    public SpeciesAbundanceCheck() {
        super("total");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (target.getMethod() != 1 &&
                target.getMethod() != 2)
            return Validated.valid("No affected");
        return target.getSpeciesAttributesOpt().map(uiSpeciesAttributes -> {
            val maxAbundance = uiSpeciesAttributes.getMaxAbundance();
            if (maxAbundance == null || target.getTotal() <= maxAbundance) {
                return Validated.<StagedRowError, String>valid("Total under MaxAbundance");
            }
            return invalid(target,
                    "Total is above max abundance [" + maxAbundance + "] for Species [" + target.getSpeciesAttributesOpt().getSpeciesName() + "].",
                    ValidationCategory.DATA,
                    ValidationLevel.WARNING, Optional.empty());

        }).orElseGet(() -> Validated.<StagedRowError, String>valid("Not Affected"));
    }


}
