package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

public class SpeciesNotFound extends BaseFormattedValidator {

    public SpeciesNotFound() {
        super("Species");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val sum = target.getMeasureJson()
                .entrySet().stream()
                .map(Map.Entry::getValue)
                .reduce(0, Integer::sum);

        if (target.getSpecies().getObservableItemName().equalsIgnoreCase("Species Not Found")) {
            if (sum == 0) {
                return Validated.valid("Species Not found is valid");
            }
            return Validated.invalid(new StagedRowError(
                    new ErrorID(target.getId(),
                            target.getRef().getStagedJob().getId(),
                            "'Species not found'should have no measure"),
                    ValidationCategory.DATA,
                    ValidationLevel.BLOCKING,
                    columnTarget,
                    target.getRef()));
        }
        return Validated.valid("No Affected");
    }
}
