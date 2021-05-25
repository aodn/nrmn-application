package au.org.aodn.nrmn.restapi.validation.validators.row.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;
import lombok.val;

import java.util.Optional;

public class Within200MSiteCheck extends BaseFormattedValidator {
    public Within200MSiteCheck() {
        super("Longitude,Latitude");
    }

    private static double _distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            // 60 * 1.1515 * 1.609344;
            return Math.toDegrees(Math.acos(dist)) * 111.18957696;
        }
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        val dist = _distance(
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
