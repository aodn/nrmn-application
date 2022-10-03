package au.org.aodn.nrmn.restapi.service.validation;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistanceLatLongMeters;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;

@Service
public class SiteValidation {

    // VALIDATION: Survey coordinates match site coordinates
    public SurveyValidationError validateSurveyAtSite(StagedRowFormatted row) {

        var distMeters = getDistanceLatLongMeters(row.getSite().getLatitude(), row.getSite().getLongitude(), row.getLatitude(), row.getLongitude());

        // Warn if survey is more than 10 meters away from site
        if (distMeters > 10) {
            var message = "Survey coordinates more than 10m from site (" + String.format("%.1f", distMeters) + "m)";
            return new SurveyValidationError(ValidationCategory.DATA, ValidationLevel.WARNING, message, Arrays.asList(row.getId()),Arrays.asList("latitude","longitude"));
        }

        return null;
    }
}
