package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
                stage.setLMax("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormat(StagedRow::getLMax, "Lmax").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSuccess() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setLMax("10");
        stage.setStagedJob(job);
        val res = new IntegerFormat(StagedRow::getLMax, "Lmax").valid(stage);
        assertTrue(res.isValid());
    }


}