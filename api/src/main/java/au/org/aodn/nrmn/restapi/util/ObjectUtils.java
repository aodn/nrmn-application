package au.org.aodn.nrmn.restapi.util;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

public class ObjectUtils {

    public static <T> Boolean stringPropertiesDiffer(Function<T, String> getter, T rowA, T rowB) {
        return stringPropertiesDiffer(false, getter, getter, rowA, rowB);
    }

    public static <T,U> Boolean stringPropertiesDiffer(Boolean justValue, Function<T, String> getterA, Function<U, String> getterB, T rowA, U rowB) {
        var valueA = getterA.apply(rowA);
        var valueB = getterB.apply(rowB);
        var contentDiffers = (valueA == null && valueB != null) || (valueB == null && valueA != null);
        var contentEmpty = !contentDiffers && StringUtils.isEmpty(valueA);
        var valueDiffers = StringUtils.isNotEmpty(valueA)
                && StringUtils.isNotEmpty(valueB)
                && (valueA != null && !valueA.equalsIgnoreCase(valueB));
        return justValue ? (contentDiffers || valueDiffers) : (contentDiffers || valueDiffers || contentEmpty);
    }
}
