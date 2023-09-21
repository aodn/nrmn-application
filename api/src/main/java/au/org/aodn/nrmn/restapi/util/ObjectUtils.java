package au.org.aodn.nrmn.restapi.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
    /**
     * This copy is null aware, which means if value is null, it will not copy
     */
    public static BeanUtilsBean createNullAwareBeanUtils(String... ignoreFields) {
        return new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
                if(value==null || Arrays.stream(ignoreFields).anyMatch(p -> p.equalsIgnoreCase(name))) return;
                super.copyProperty(dest, name, value);
            }
        };
    }
    /**
     * This copy function copy value when destination field is null
     * @param zeroEqualsNull - If true, when income value is zero, set it to null
     */
    public static BeanUtilsBean createNullCopyBeanUtils(boolean zeroEqualsNull, String... ignoreFields) {
        return new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
                try {
                    String destValue = BeanUtils.getProperty(dest, name);
                    if(!Arrays.stream(ignoreFields).anyMatch(p -> p.equalsIgnoreCase(name))) {
                        Double v = null;
                        try {
                            v = Double.valueOf(destValue);
                        }
                        catch(Exception e) {}

                        if(zeroEqualsNull && v != null && v == 0) {
                            // Value is zero and flag is on, so set the zero value in dest to null
                            super.copyProperty(dest, name, null);
                        }
                        else {
                            if (destValue == null) {
                                // Copy value since dest is null
                                super.copyProperty(dest, name, value);
                            }
                        }

                    }
                }
                catch (NoSuchMethodException e) {
                    return;
                }
            }
        };
    }
}
