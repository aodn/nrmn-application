package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.stream.Collectors;

public class MeasureUnderLmax extends BaseFormattedValidator {
    public MeasureUnderLmax() {
        super("measure");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (!target.getRef().getStagedJob().getIsExtendedSize()) {
            return Validated.valid("not affected");
        }

        if (!target.getSpeciesAttributesOpt().isPresent()) {
            return Validated.valid("No Species Data");
        }

        val speciesAttributes =  target.getSpeciesAttributesOpt().get();
        val lmax = speciesAttributes.getLmax();
        val outOfRange = target.getMeasureJson().entrySet().stream()
                .filter(entry -> entry.getValue() > lmax)
                .collect(Collectors.toList());


        if (outOfRange.isEmpty()) {
            return Validated.valid("Values under Lmax");
        }
        val keysStr = outOfRange.stream().map(Object::toString)
                .reduce("", (acc, value) -> acc + "|" + value);
        return Validated.invalid(
                new StagedRowError(
                        new ErrorID(target.getId(),
                                target.getRef().getStagedJob().getId(),
                                "measure above Lmax"),
                        ValidationCategory.DATA,
                        ValidationLevel.WARNING,
                        columnTarget + ":" + keysStr,
                        target.getRef())
        );
    }
}
