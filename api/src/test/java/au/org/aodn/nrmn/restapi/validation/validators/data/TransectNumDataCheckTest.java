package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.validators.data.TransectNumDataCheck;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransectNumDataCheckTest {

    @Test
    void depthWithoutTransectShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("8,3");
        stage.setStagedJob(job);
        val res = new TransectNumDataCheck().valid(stage);
        assertTrue(res.isInvalid());

    }
    @Test
    void depthWithTransectOutOfRangeShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("10.9");
        stage.setStagedJob(job);
        val res = new TransectNumDataCheck().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void depthWithTransectInRangeShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("7.3");
        stage.setStagedJob(job);

        val res = new TransectNumDataCheck().valid(stage);
        assertTrue(res.isValid());
    }
}
