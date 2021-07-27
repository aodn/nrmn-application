package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

// import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesNotSuperseeded;
import lombok.val;

class SpeciesNotSuperseededTest extends FormattedTestProvider {

    @Test
    public void notSupperSeededShouldFSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setIsInvertSizing(false);
        // val validationRule = new SpeciesNotSuperseeded();
        // val res = validationRule.valid(formatted);
        // assertTrue(res.isValid());
    }
}
