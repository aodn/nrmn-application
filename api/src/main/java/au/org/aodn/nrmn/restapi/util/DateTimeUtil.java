package au.org.aodn.nrmn.restapi.util;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static Date truncateAfterMinute(Date date) {
        return DateUtils.truncate(date, Calendar.MINUTE);
    }
}
