package au.org.aodn.nrmn.restapi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;

/**
 * Helper methods for parsing times for supported formats
 */
 
public class TimeUtils {
    private static final List<DateTimeFormatter> SUPPORTED_LOCALTIME_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("H:mm[:ss]"),
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h:mm[:ss][[ ]a]")
                    .toFormatter());

    private static final String SUPPORTED_DATE_FORMAT = "yyyy-MM-dd";

    public static LocalTime parseTime(String value) {
        for (DateTimeFormatter supportedFormat: SUPPORTED_LOCALTIME_FORMATS) {
            try {
                return LocalTime.parse(value, supportedFormat);
            } catch (DateTimeParseException e) {}
        }

        throw new DateTimeException("Invalid time");
    }

    public static Date parseDate(String value) {

        if( value == null){
            return null;
        }

        Date date;

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(SUPPORTED_DATE_FORMAT);
        sdf.setLenient(false);
        try {
            date = new Date(sdf.parse(value).getTime());
        } catch (ParseException ignored) {
            throw new DateTimeException("Invalid date. The date must be in the format yyyy-mm-dd");
        }

        return date;
    }

}
