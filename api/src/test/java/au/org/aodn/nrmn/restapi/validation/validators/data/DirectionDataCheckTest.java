package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.validators.data.DirectionDataCheck;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionDataCheckTest {
    @Test
    void invalidDirectionShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setDirection("ED");
        val res = new DirectionDataCheck().valid(stage);
        assertTrue(res.isInvalid());



    }
    @Test
    void directionShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setDirection("NE");
        val res = new DirectionDataCheck().valid(stage);
        assertTrue(res.isValid());

    }
}