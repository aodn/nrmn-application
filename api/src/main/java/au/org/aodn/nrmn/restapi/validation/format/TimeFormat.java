package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import cyclops.control.Validated;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class TimeFormat extends BaseValidationFormat {
    TimeFormat() {
        this.format = "HH:mm";
        this.columnTarget = "Time";
    }

    @Override
    public Validated<ErrorCheckEntity, String> valid(StagedSurveyEntity target) {
        return validFormat(
                StagedSurveyEntity::getTime,
                timeString -> {
                    LocalTime localTime = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                    localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    localTime.get(ChronoField.MINUTE_OF_HOUR);
                }, target);
    }

}
