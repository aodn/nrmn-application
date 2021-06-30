package au.org.aodn.nrmn.restapi.validation.validators.global.formatted;

import au.org.aodn.nrmn.restapi.validation.validators.formatted.FormattedTestProvider;
import lombok.val;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DuplicateRowCheckTest extends FormattedTestProvider {

    @Test
    void duplicateRowShouldFail() {
        val r1 = getDefaultFormatted().build();
        r1.setBlock(1);
        r1.getRef().setPos(1);
        val r2 = getDefaultFormatted().build();
        r2.setBlock(1);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(3, 8).put(50, 5).build());
        r2.getRef().setPos(2);
        val r3 = getDefaultFormatted().build();
        r3.setBlock(3);
        r3.getRef().setPos(3);

        val list = Arrays.asList(r1, r2, r3);
        val validator = new DuplicateRowCheck();
        val res = validator.valid(r1.getRef().getStagedJob(), list);
        assertTrue(res.isInvalid());
    }

    @Test
    void duplicateButNotSequentialRowShouldSucceed() {
        val r1 = getDefaultFormatted().build();
        r1.setBlock(1);
        r1.getRef().setPos(1);
        val r2 = getDefaultFormatted().build();
        r2.setBlock(2);
        r2.setMeasureJson(ImmutableMap.<Integer, Integer>builder().put(3, 8).put(50, 5).build());
        r2.getRef().setPos(2);
        val r3 = getDefaultFormatted().build();
        r3.setBlock(1);
        r3.getRef().setPos(3);

        val list = Arrays.asList(r1, r2, r3);
        val validator = new DuplicateRowCheck();
        val res = validator.valid(r1.getRef().getStagedJob(), list);
        assertTrue(res.isValid());
    }

    @Test
    void nonDuplicatedRowsShouldSucceed() {
        val r1 = getDefaultFormatted().build();
        r1.setBlock(1);
        val r2 = getDefaultFormatted().build();
        r2.setBlock(2);
        val r3 = getDefaultFormatted().build();
        r3.setBlock(3);

        val list = Arrays.asList(r1, r2, r3);

        val validator = new Method3QuadratsMissing();
        val res = validator.valid(r1.getRef().getStagedJob(), list);
        assertTrue(res.isValid());

    }
}
