package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.formatted.FormattedTestProvider;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Method3QuadratsMissingTest extends FormattedTestProvider {

    @Test
    void transectWithAllQuadratsFilledShouldSuccess() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(2, 2).build());
        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(4, 1).put(3, 2).build());
        val r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(5, 19).build());
        val list = Arrays.asList(r1, r2, r3);

        // val validator = new Method3QuadratsMissing();
        // val res = validator.valid(r1.getRef().getStagedJob(), list);
        // assertTrue(res.isValid());

    }

    @Test
    void transectWithMissingQuadratsFilledShouldSuccess() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(2, 2).build());
        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(4, 1).put(3, 2).build());

        val list = Arrays.asList(r1, r2);

        // val validator = new Method3QuadratsMissing();
        // val res = validator.valid(r1.getRef().getStagedJob(), list);
        // assertTrue(res.isInvalid());

    }
}
