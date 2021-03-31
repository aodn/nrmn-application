package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasureBetweenL5l95Test extends FormattedTestProvider{


    @Test
    public void outOfScopeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new MeasureBetweenL5l95();
        formatted.setMethod(3);
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
}
