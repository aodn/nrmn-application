package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesNotSuperseededTest extends FormattedTestProvider {

    @Test
    public void notSupperSeededShouldFSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setIsInvertSizing(false);
        val validationRule = new SpeciesNotSuperseeded();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
}