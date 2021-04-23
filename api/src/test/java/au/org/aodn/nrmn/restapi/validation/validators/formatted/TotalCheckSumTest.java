package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

class TotalCheckSumTest extends FormattedTestProvider  {
    @Test
    public void validSumShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(1, 0).put(3, 1).put(4, 2).build());
        formatted.setTotal(3);
        val validationRule = new TotalCheckSum();
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

    @Test
    public void InValidSumShouldFailed() {
        val formatted = getDefaultFormatted().build();
        formatted.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(0, 1).put(3, 1).put(4, 2).build());
        formatted.setTotal(3);
        val validationRule = new TotalCheckSum();
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }
}