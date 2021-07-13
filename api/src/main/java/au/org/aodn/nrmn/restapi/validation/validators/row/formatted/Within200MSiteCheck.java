package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.Optional;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistance;

public class Within200MSiteCheck extends BaseFormattedValidator {
    public Within200MSiteCheck() {
        super("Longitude,Latitude");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val dist = getDistance(
                target.getSite().getLatitude(),
                target.getSite().getLongitude(),
                target.getLatitude(),
                target.getLongitude());
        if (dist <= 0.2) {
            return Validated.valid("coords in range");
        }
        return invalid(target, "Coordinates are further than 0.2km from the Site (" + dist + "km)", ValidationCategory.DATA, ValidationLevel.WARNING, Optional.empty());
    }
}
