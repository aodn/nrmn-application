package au.org.aodn.nrmn.restapi.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;

/**
 * Helper methods for parsing times for supported formats
 */

public class TimeUtils {
    private static final DateTimeFormatter SUPPORTED_TIME_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("[h:mm[:ss] a][H:mm[:ss]]").toFormatter();
    
    private static final DateTimeFormatter ROW_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("d/M/[yyyy][yy]").toFormatter();

    private static final String SUPPORTED_DATE_FORMAT = "yyyy-MM-dd";

    public static Optional<LocalTime> parseTime(String value) {
        try {
            return Optional.of(LocalTime.parse(value, SUPPORTED_TIME_FORMATTER));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static DateTimeFormatter getRowDateFormatter(){
        return ROW_DATE_TIME_FORMATTER;
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
        } catch (Exception ignored) {
            throw new DateTimeException("Invalid date. The date must be in the format yyyy-mm-dd");
        }

        return date;
    }

}
