package au.org.aodn.nrmn.restapi.util;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtils {

    public static <T> Boolean stringPropertiesDiffer(Function<T, String> getter, T rowA, T rowB) {
        var valueA = getter.apply(rowA);
        var valueB = getter.apply(rowB);
        var contentDiffers = (valueA == null && valueB != null) || (valueB == null && valueA != null);
        var valueDiffers = StringUtils.isNotEmpty(valueA)
                && StringUtils.isNotEmpty(valueB)
                && !valueA.equalsIgnoreCase(valueB);
        return contentDiffers || valueDiffers;
    }

}
