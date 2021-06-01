package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import java.util.Map;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import cyclops.control.Validated;

public class MissingDataCheck extends BaseFormattedValidator {

    public static final int OBS_ITEM_TYPE_NO_SPECIES_FOUND = 6;

    public MissingDataCheck() {
        super("MissingDataCheck");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {

        // If this row is 'Survey Not Done' or 'No Species Found' then do not require
        // any observations
        if (target.getCode().equalsIgnoreCase("SND")
                || (target.getSpecies().get().getObsItemType().getObsItemTypeId() == OBS_ITEM_TYPE_NO_SPECIES_FOUND)) {
            return Validated.valid("Row not required to have data");
        }

        int observationTotal = target.getMeasureJson().entrySet().stream().map(Map.Entry::getValue).reduce(0,
                Integer::sum);

        if (observationTotal > 0)
            return Validated.valid("Observations exist");
        else
            return Validated.invalid(new StagedRowError(
                    new ErrorID(target.getId(), target.getRef().getStagedJob().getId(),
                            "Record has no data and but not flagged as 'Survey Not Done' or 'No Species Found'"),
                    ValidationCategory.DATA, ValidationLevel.WARNING, columnTarget, target.getRef()));
    }
}
