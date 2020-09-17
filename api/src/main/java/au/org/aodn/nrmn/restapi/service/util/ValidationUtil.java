package au.org.aodn.nrmn.restapi.service.util;

import au.org.aodn.nrmn.restapi.exception.ValidationException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class ValidationUtil {

    private static List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());

    public static void versionCheck(long newVersion, long oldVersion) {
        if (newVersion != oldVersion) {
            throw new OptimisticLockingFailureException(
                    String.format("version given %s does not match database version %s", newVersion, oldVersion));
        }
    }

    public static void timezoneIsValidCheck(String timezone) {
        if (!timezones.contains(timezone)) {
            throw new ValidationException(String.format("%s is not a valid timezone", timezone));
        }
    }
}
