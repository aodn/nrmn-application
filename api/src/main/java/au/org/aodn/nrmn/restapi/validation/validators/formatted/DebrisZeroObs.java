package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.Map;

public class DebrisZeroObs extends BaseFormattedValidator {
    public DebrisZeroObs() {
        super("Species");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (target.getSite().getSiteCode().equalsIgnoreCase("dez") &&
            target.getSpecies().getObservableItemName().equalsIgnoreCase("Debris-Zero"))     {
            val sum = target.getMeasureJson()
                    .entrySet().stream()
                    .map(Map.Entry::getValue)
                    .reduce(0, Integer::sum);
            if (target.getInverts() == 0 && target.getTotal() == 0 && sum == 0) {
                return Validated.valid("Debris is valid");
            }
            return Validated.invalid(
                    new StagedRowError(
                            new ErrorID(target.getId(),
                                    target.getRef().getStagedJob().getId(),
                                    "Debris  has Value/Total/Inverts not 0 "),
                            ValidationCategory.DATA,
                            ValidationLevel.BLOCKING,
                            columnTarget,
                            target.getRef())
            );
        }
        return Validated.valid("Not Affected");
    }
}
