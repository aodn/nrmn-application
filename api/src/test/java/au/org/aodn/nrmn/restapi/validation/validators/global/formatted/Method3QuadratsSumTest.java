package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.formatted.FormattedTestProvider;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Method3QuadratsSumTest extends FormattedTestProvider {
    @Test
    void quadratsSumUnder50ShouldFail() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(2, 2).build());

        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(3, 8).put(50, 5).build());

        val r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(3, 4).build());

        val r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build());

        val date = LocalDate.now();

        val a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 4).put(3, 7).build());

        val a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(2, 3).build());

        val a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(4, 10).build());

        val a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(5, 6).build());

        val list = Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4);
        val validator = new Method3QuadratsSum();
        val res = validator.valid(r1.getRef().getStagedJob(), list);
        assertTrue(res.isInvalid());
    }

    @Test
    void quadratsSumUnder50ShouldSuccess() {
        val r1 = getDefaultFormatted().build();
        r1.setMethod(3);
        r1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 40).put(2, 20).build());

        val r2 = getDefaultFormatted().build();
        r2.setMethod(3);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(3, 80).put(4, 45).put(5, 30).build());

        val r3 = getDefaultFormatted().build();
        r3.setMethod(3);
        r3.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(2,10).put(4, 6).build());

        val r4 = getDefaultFormatted().build();
        r4.setMethod(3);
        r4.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(2,20).put(1, 140).put(5, 27).build());

        val date = LocalDate.now();

        val a1 = getDefaultFormatted().build();

        a1.setMethod(3);
        a1.setDate(date);
        a1.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 42).put(3, 70).build());

        val a2 = getDefaultFormatted().build();
        a2.setMethod(3);
        a2.setDate(date);
        a2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1,10).put(2, 35).build());

        val a3 = getDefaultFormatted().build();
        a3.setMethod(3);
        a3.setDate(date);
        a3.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(2,16).put(4, 100).put(5,52).build());

        val a4 = getDefaultFormatted().build();
        a4.setMethod(3);
        a4.setDate(date);
        a4.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(5, 6).build());

        val list = Arrays.asList(r1, r2, r3, r4, a1, a2, a3, a4);
        val validator = new Method3QuadratsSum();
        val res = validator.valid(r1.getRef().getStagedJob(), list);
        assertTrue(res.isValid());
    }
}
