package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.SpeciesNotSuperseeded;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesNotSuperseededTest extends FormattedTestProvider {

    @Test
    public void notSupperSeededShouldFSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setIsInvertSizing(Optional.of(false));
        val validationRule = new SpeciesNotSuperseeded();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
}
