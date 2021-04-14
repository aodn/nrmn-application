package au.org.aodn.nrmn.restapi.validation.validators.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ATRCDepthValidationTest {

    @Test
    void depthWithoutTransectShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("8,3");
        stage.setStagedJob(job);
        val res =
                new ATRCDepthValidation().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void depthWithTransectOutOfRangeShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("10.9");
        stage.setStagedJob(job);
        val res =
                new ATRCDepthValidation().valid(stage);;
        assertTrue(res.isInvalid());

    }

    @Test
    void depthWithTransectInRangeForMethod1ShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("7.3");
        stage.setStagedJob(job);
        stage.setMethod("1");

        val res =
                new ATRCDepthValidation().valid(stage);;
        assertTrue(res.isValid());
    }

    @Test
    void depthWithNoTransectForMethod0ShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("7");
        stage.setStagedJob(job);
        stage.setMethod("0");

        val res =
                new ATRCDepthValidation().valid(stage);;
        assertTrue(res.isValid());
    }

}
