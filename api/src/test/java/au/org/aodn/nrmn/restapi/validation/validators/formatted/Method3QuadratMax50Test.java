package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.Method3QuadratMax50;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

class Method3QuadratMax50Test extends FormattedTestProvider{
    @Test
    public void outOfScopeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new Method3QuadratMax50();
        formatted.setMethod(2);
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void method3WithQuadratsUnder50ShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new Method3QuadratMax50();
        formatted.setMethod(3);
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder()
                .put(1, 30)
                .put(3, 15)
                .put(4, 20)
                .put(5, 49)
                .put(2, 20).build());

        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }
    @Test
    public void method3WithQuadratsAbove50ShouldFailed() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new Method3QuadratMax50();
        formatted.setMethod(3);
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder()
                .put(1, 100)
                .put(4, 20)
                .put(5, 50)
                .put(2, 20).build());

        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }
}
