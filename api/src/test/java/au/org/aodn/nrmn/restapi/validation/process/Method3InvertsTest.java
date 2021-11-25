package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

class Method3InvertsTest extends FormattedTestProvider {

    @Test
    void method3WithInvertsShouldFail() {
        StagedRowFormatted formatted = getDefaultFormatted().build();
        formatted.setMethod(3);
        formatted.setInverts(5);
        Collection<ValidationError> errors = validationProcess.checkData("ATRC", false, Arrays.asList(formatted));
        assertTrue(errors.stream().anyMatch(p -> p.getMessage().contains("Method 3")));
    }
}
