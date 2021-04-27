package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

/*
   waiting for more spec
 */
public class SpeciesNotFoundCheck extends BaseRowValidator {

    public SpeciesNotFoundCheck() {
        super("species");
    }

    @Override
    public Validated<StagedRowError, Boolean> valid(StagedRow target) {
        val filteredEntry = target
                .getMeasureJson()
                .entrySet()
                .stream()
                .filter(entry ->
                        StringUtils.isNumeric(entry.getValue()) &&
                                !entry.getValue().trim().equals("0"))
                .collect(Collectors.toList());
        if (target.getSpecies().trim().equalsIgnoreCase("No Species Found")) {
            if (filteredEntry.size() == 0) {
                return Validated.valid(true);
            }
            return Validated.invalid(new StagedRowError(
                    new ErrorID(target.getId(),
                            target.getStagedJob().getId(),
                            target.getSpecies() + " should have no measure"),
                    ValidationCategory.DATA,
                    ValidationLevel.WARNING,
                    columnTarget,
                    target));
        }
        return Validated.valid(false);
    }
}
