package au.org.aodn.nrmn.restapi.util;

public class Constants {

    // If the distance between the survey and site coordinates is less than this value, the survey will use the site's coordinates
    public static final int SURVEY_LOCATION_TOLERANCE = 10;

    // The number of decimal places to use when validating coordinates
    public static final int COORDINATE_VALID_DECIMAL_COUNT = 5;

    public static final String ROUND_LON_MSG = "Longitude will be rounded to 5 decimal places";
    public static final String ROUND_LAT_MSG = "Latitude will be rounded to 5 decimal places";
    public static final String NULLIFY_LAT_LON_MSG_SUFFIX = "This row will use the site's coordinates.";
}
