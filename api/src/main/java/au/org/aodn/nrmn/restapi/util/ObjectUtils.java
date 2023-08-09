package au.org.aodn.nrmn.restapi.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
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
     */
    public static BeanUtilsBean createNullCopyBeanUtils(String... ignoreFields) {
        return new BeanUtilsBean() {
            @Override
            public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
                try {
                    if(BeanUtils.getProperty(dest, name) != null || Arrays.stream(ignoreFields).anyMatch(p -> p.equalsIgnoreCase(name))) return;
                    super.copyProperty(dest, name, value);
                }
                catch (NoSuchMethodException e) {
                    return;
                }
            }
        };
    }
}
