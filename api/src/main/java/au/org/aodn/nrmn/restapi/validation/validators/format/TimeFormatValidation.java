package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;

import cyclops.control.Validated;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class TimeFormatValidation extends BaseRowFormatValidation<LocalTime> {
    public TimeFormatValidation() {
        super("Time", "H:mm");
    }

    @Override
    public Validated<StagedRowError, LocalTime> valid(StagedRow target) {
        return validFormat(
                StagedRow::getTime,
                timeString -> {
                    LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                    localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    localTime.get(ChronoField.MINUTE_OF_HOUR);
                    return Validated.valid(localTime);
                }, target);
    }

}
