package au.org.aodn.nrmn.restapi.util;

public class NumberUtils {

    public static int getDecimalCount(double number) {
        String text = Double.toString(Math.abs(number));
        int integerPlaces = text.indexOf('.');
        return text.length() - integerPlaces - 1;
    }
}
