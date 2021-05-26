package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.row.formatted.TooOldFutureDate;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TooOldFutureDateTest extends FormattedTestProvider {
    @Test
    void dateOutRangeShouldFail() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new TooOldFutureDate("2006-01-01");
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }

    @Test
    void futureDateShouldFail() {
        val formatted = getDefaultFormatted().build();
        formatted.setDate(LocalDate.of(4003, 03, 03));

        val validationRule = new TooOldFutureDate("2006-01-01");
        val res = validationRule.valid(formatted);
        assertTrue(res.isInvalid());
    }

    @Test
    void dateInRangeShouldSuccess() {
        val formatted = getDefaultFormatted().build();
        val validationRule = new TooOldFutureDate("2000-01-01");
        val res = validationRule.valid(formatted);
        assertTrue(res.isValid());
    }

}
