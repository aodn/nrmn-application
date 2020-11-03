package au.org.aodn.nrmn.restapi.validation.data;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.model.db.composedID.ErrorID;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import cyclops.control.Validated;
import org.apache.commons.lang3.EnumUtils;


public class DirectionDataCheck extends BaseRowValidator {
    public DirectionDataCheck() {
        super("Direction");
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        if (EnumUtils.isValidEnum(Directions.class, target.getDirection()))
            return Validated.valid("Direction format is valid");
        return Validated.invalid(
                new ErrorCheck(
                        new ErrorID(target.getId(),
                                target.getStagedJob().getId(),
                                columnTarget + "format should be a valid direction: N,NE,E,SE,S,SW,W,NW"),
                        ValidationCategory.DATA,
                        columnTarget,
                        target));
    }
}
