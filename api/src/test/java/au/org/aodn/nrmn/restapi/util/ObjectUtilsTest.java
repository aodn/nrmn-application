package au.org.aodn.nrmn.restapi.util;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.assertTrue;

public class ObjectUtilsTest {
    /**
     * Verify the behavior of copy, it should be if dest field is null then copy value as null indicate missing,
     * if the value is 0, then depends on flag, can assume value 0 in destination means set it to null instead.
     */
    @Test
    public void verifyCreateNullCopyBeanUtils() throws InvocationTargetException, IllegalAccessException {

        BeanUtilsBean nullCopyBeanUtilsZeroNotNull = ObjectUtils.createNullCopyBeanUtils(false);
        BeanUtilsBean nullCopyBeanUtilsZeroNull = ObjectUtils.createNullCopyBeanUtils(true);

        ObservableItemPutDto source = new ObservableItemPutDto();
        source.setObservableItemId(1213);
        source.setLengthWeightA(123.0);
        source.setClassName("1234");

        ObservableItemPutDto dest = new ObservableItemPutDto();

        nullCopyBeanUtilsZeroNotNull.copyProperties(dest, source);
        assertTrue("1. d1", source.getObservableItemId().equals(dest.getObservableItemId()));
        assertTrue("1. i1", source.getLengthWeightA().equals(dest.getLengthWeightA()));
        assertTrue("1. s1", source.getClassName().equals(dest.getClassName()));

        dest = new ObservableItemPutDto();
        dest.setLengthWeightA(0.0);
        dest.setObservableItemId(0);

        // Copy not happen because some field have value.
        nullCopyBeanUtilsZeroNotNull.copyProperties(dest, source);
        assertTrue("2. d1 not copy as it have value", dest.getLengthWeightA().equals(0.0));
        assertTrue("2. i1 not copy as it have value", dest.getObservableItemId().equals(0));
        assertTrue("2. s1", source.getClassName().equals(dest.getClassName()));

        // Redo the copy will now set the 0 to null due to flag setup
        nullCopyBeanUtilsZeroNull.copyProperties(dest, source);
        assertTrue("3. d1 value null due to flag", dest.getLengthWeightA() == null);
        assertTrue("3. i1 value null due to flag", dest.getObservableItemId() == null);

    }
}
