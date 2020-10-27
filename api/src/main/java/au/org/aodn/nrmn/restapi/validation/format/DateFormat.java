package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import cyclops.control.Validated;


import java.text.SimpleDateFormat;

public final class DateFormat extends BaseValidationFormat {

    DateFormat() {
        this.format = "dd/MM/yyyy";
        this.columnTarget = "Date";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        return validFormat(
                StagedSurveyEntity::getDate,
                dateString -> {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    formatter.parse(dateString);
                }, target);
    }
}
