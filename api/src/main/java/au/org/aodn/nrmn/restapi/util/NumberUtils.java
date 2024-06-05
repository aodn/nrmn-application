package au.org.aodn.nrmn.restapi.util;

import org.apache.commons.math3.util.Precision;


public class NumberUtils {

    public static int getDecimalCount(double number) {
        String text = Double.toString(Math.abs(number));
        int integerPlaces = text.indexOf('.');
        return text.length() - integerPlaces - 1;
    }

    public static String roundDecimalString(String decimalString, int decimalPlaces) {
        try {
            var doubleValue = Double.parseDouble(decimalString);
            var roundedValue = Precision.round(doubleValue, decimalPlaces);

            // if the value is an integer, return it as an integer (otherwise it will be returned as a double with a .0 at the end)
            if (roundedValue == Math.floor(roundedValue)) {
                return String.valueOf((int) roundedValue);
            }
            return Double.toString(roundedValue);
        } catch (NumberFormatException e) {
            return decimalString;
        }
    }
}
