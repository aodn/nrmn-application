package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import cyclops.control.Validated;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class TimeFormat extends BaseRowValidationFormat {
    TimeFormat() {
        super("Time", "HH:mm");
    }

    @Override
    public Validated<ErrorCheck, String> valid(StagedSurvey target) {
        return validFormat(
                StagedSurvey::getTime,
                timeString -> {
                    LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                    localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    localTime.get(ChronoField.MINUTE_OF_HOUR);
                    return Validated.valid(localTime);
                }, target);
    }

}
