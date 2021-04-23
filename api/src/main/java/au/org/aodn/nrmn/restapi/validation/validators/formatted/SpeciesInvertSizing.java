package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

public class SpeciesInvertSizing extends BaseFormattedValidator {
    public SpeciesInvertSizing() {
        super("IsInvertSized");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {


        if (!target.getRef().getStagedJob().getIsExtendedSize()) {
            return Validated.valid("Not affected");
        }
        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes =  target.getSpeciesAttributesOpt().get();

        val isInverted = speciesAttributes.getIsInvertSized();

        if (isInverted == target.getIsInvertSizing()) {
            return Validated.valid("IsInvertSizing valid");
        }
    return Validated.invalid(new StagedRowError(
            new ErrorID(target.getId(),
                    target.getRef().getStagedJob().getId(),
                    "IsInvertSized didn't match  database"),
            ValidationCategory.DATA,
            ValidationLevel.WARNING,
            columnTarget,
            target.getRef()));
    }
}
