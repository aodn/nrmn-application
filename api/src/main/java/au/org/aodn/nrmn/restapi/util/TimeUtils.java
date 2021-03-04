package au.org.aodn.nrmn.restapi.util;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Helper methods for parsing times for supported formats
 */
 
public class TimeUtils {
    private static final List<DateTimeFormatter> SUPPORTED_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("H:mm[:ss]"),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mm[:ss][[ ]a]")
                    .toFormatter());

    public static LocalTime parseTime(String value) {
        for (DateTimeFormatter supportedFormat: SUPPORTED_FORMATS) {
            try {
                return LocalTime.parse(value, supportedFormat);
            } catch (DateTimeParseException e) {}
        }

        throw new DateTimeException("Invalid time");
    }

}
