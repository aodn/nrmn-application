package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DoubleFormatTest {
    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setLongitude("Not a number");
        stage.setStagedJob(job);
        val res = new DoubleFormat(StagedRow::getLongitude, "Longitude").valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void minusValueShouldBeOK() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setLongitude("-67.192519");
        stage.setStagedJob(job);
        val res = new DoubleFormat(StagedRow::getLongitude, "Longitude").valid(stage);
        assertTrue(res.isValid());
    }
}
