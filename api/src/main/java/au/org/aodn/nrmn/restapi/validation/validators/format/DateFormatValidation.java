package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;


import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormatValidation extends BaseRowFormatValidation<Date> {

    public DateFormatValidation() {
        super("Date", "dd/MM/yyyy");
    }

    @Override
    public  Validated<StagedRowError, Date> valid(StagedRow target) {
        return validFormat(
                StagedRow::getDate,
                dateString -> {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    return Validated.valid(formatter.parse(dateString));

                }, target);
    }
}
