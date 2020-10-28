package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeFormatTest {

    @Test
    void incorrectTimeFormatShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setTime("ti:me");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void quarterPastTenShouldBeOk() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setTime("10:15");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isValid());

    }

    @Test
    void beyoundBoundaryShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setTime("40:15");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isInvalid());
    }
}