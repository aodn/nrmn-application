package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalIntegerFormatTest {

    @Test
    void missingValueShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setVis("");
        stage.setStagedJob(job);
        val res = new OptionalIntegerFormatValidation(StagedRow::getLMax, "Vis").valid(stage);
        assertTrue(res.isValid());
    }

}
