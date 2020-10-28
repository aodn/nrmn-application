package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Validated;


import java.text.SimpleDateFormat;

public final class DateFormat extends BaseValidationFormat {

    DateFormat() {
        super("Date", "dd/MM/yyyy");
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(
                StagedSurvey::getDate,
                dateString -> {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    formatter.parse(dateString);
                }, target);
    }
}
