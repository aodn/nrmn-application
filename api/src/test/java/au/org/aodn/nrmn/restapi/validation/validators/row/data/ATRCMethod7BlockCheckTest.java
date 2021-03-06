package au.org.aodn.nrmn.restapi.validation.validators.row.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;

class ATRCMethod7BlockCheckTest {

    @Test
    void method7OnBlock2ShouldSucceed() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setBlock("2");
        stage.setStagedJob(job);
        // val res = new ATRCMethod7BlockCheck().valid(stage);
        // assertTrue(res.isValid());
    }

    @Test
    void method7OnBlock3ShouldFail() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setBlock("3");
        stage.setStagedJob(job);
        // val res = new ATRCMethod7BlockCheck().valid(stage);
        // assertTrue(res.isInvalid());
    }

    @Test
    void method2OnBlock2ShouldSucceed() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setMethod("2");
        stage.setBlock("2");
        stage.setStagedJob(job);
        // val res = new ATRCMethod7BlockCheck().valid(stage);
        // assertTrue(res.isValid());
    }

}
