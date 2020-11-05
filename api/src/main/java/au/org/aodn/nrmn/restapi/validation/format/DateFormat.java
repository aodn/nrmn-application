package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;


import java.text.SimpleDateFormat;

public final class DateFormat extends BaseRowValidationFormat {

    DateFormat() {
        super("Date", "dd/MM/yyyy");
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRow target) {
        return validFormat(
                StagedRow::getDate,
                dateString -> {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    return Validated.valid(formatter.parse(dateString));

                }, target);
    }
}
