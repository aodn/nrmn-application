package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import cyclops.control.Validated;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.function.Function;

public final class TimeFormatValidation extends BaseRowFormatValidation<LocalTime> {

    public TimeFormatValidation() {
        super("Time", "Time");
    }

    @Override
    public Validated<StagedRowError, Optional<LocalTime>> valid(StagedRow target) {
        if (StringUtils.isBlank(target.getTime())) {
            return Validated.valid(Optional.empty());
        }

        return validFormat(
                StagedRow::getTime,
                timeString -> {
                    LocalTime localTime = TimeUtils.parseTime(timeString);
                    localTime.get(ChronoField.CLOCK_HOUR_OF_DAY);
                    localTime.get(ChronoField.MINUTE_OF_HOUR);
                    return Validated.valid(localTime);
                }, target).bimap(Function.identity(), localTime -> Optional.of(localTime));
    }

}
