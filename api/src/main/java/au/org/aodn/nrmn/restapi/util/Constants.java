package au.org.aodn.nrmn.restapi.util;

public class Constants {

    // If the distance between the survey and site coordinates is less than this value, the survey will use the site's coordinates
    public static final int SURVEY_LOCATION_TOLERANCE = 10;

    // The number of decimal places to use when validating coordinates
    public static final int COORDINATE_VALID_DECIMAL_COUNT = 5;
}
