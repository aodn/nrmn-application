package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

public class SpeciesAbundanceCheck extends BaseFormattedValidator {
    public SpeciesAbundanceCheck() {
        super("measures");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (target.getMethod() == 1 ||
                target.getMethod() == 2 ||
                !target.getSpeciesAttributesOpt().isPresent())
            return Validated.valid("No affected");

        val maxAbundance = target.getSpeciesAttributesOpt().get().getMaxAbundance();
        if (target.getTotal() <= maxAbundance) {
            return Validated.valid("Total under MaxAbundance");
        }
        return invalid(target, "Total is above max Abudance(" + maxAbundance + ").", ValidationCategory.DATA, ValidationLevel.WARNING);
    }


}
