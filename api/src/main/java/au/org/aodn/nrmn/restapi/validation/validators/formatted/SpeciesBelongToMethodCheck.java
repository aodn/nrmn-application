package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;

public class SpeciesBelongToMethodCheck extends BaseFormattedValidator {
    public SpeciesBelongToMethodCheck() {
        super("species");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {

        val methodAttribute = target.getSpecies().getObsItemAttribute().getOrDefault("is_method", "-1");
        val methodExpected = Try
                .withCatch(() -> Integer.parseInt(methodAttribute))
                .orElseGet(() -> -1);
        if (methodExpected == -1) {
            return Validated.valid("Not affected");
        }
        if (methodExpected == target.getMethod()) {
            return Validated.valid("Species match method");
        }
        return invalid(target,
                "Species method(" + methodExpected + ") didn't match method column (" + target.getMethod() + ").",
                ValidationCategory.DATA,
                ValidationLevel.WARNING);
    }
}
