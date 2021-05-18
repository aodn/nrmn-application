package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpeciesBelongToMethodCheck extends BaseFormattedValidator {
    public SpeciesBelongToMethodCheck() {
        super("Species");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val species =  target.getSpecies();

        if (species.getMethods().stream().anyMatch(method -> method.getMethodId().equals(target.getMethod()))) {
            return Validated.valid("Species match method");
        }

        return invalid(target,
                "Species method didn't match method  (" + target.getMethod() + ").",
                ValidationCategory.DATA,
                ValidationLevel.WARNING);
    }
}
