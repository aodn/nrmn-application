package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateFormatValidation extends BaseRowFormatValidation<LocalDate> {

    public DateFormatValidation() {
        super("Date", "d/M/y");
    }

    @Override
    public  Validated<StagedRowError, LocalDate> valid(StagedRow target) {
        return validFormat(
                StagedRow::getDate,
                dateString -> {
                    DateTimeFormatter  dtf =  DateTimeFormatter.ofPattern(format);
                    return Validated.valid(LocalDate.parse(dateString, dtf));

                }, target);
    }
}
