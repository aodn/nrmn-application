package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Block0DataCheckTest {

    //the block0 check only apply if it's the block is 0
    @Test
    void blockDifferent0ShouldSuccess() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setBlock("1");
        stage.setMethod("3");
        stage.setStagedJob(job);
        val res =  new Block0DataCheck().valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void block0MethodOutOfRangeShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setBlock("0");
        stage.setMethod("2");
        stage.setStagedJob(job);
        val res =  new Block0DataCheck().valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void block0MethodInRangeShouldSuccess() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setBlock("0");
        stage.setMethod("5");
        stage.setStagedJob(job);
        val res =  new Block0DataCheck().valid(stage);
        assertTrue(res.isValid());
    }


}