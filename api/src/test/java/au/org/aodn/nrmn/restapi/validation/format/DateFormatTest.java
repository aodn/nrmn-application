package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatTest {
    @Test
    void incorrectDateFormatShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setDate("not /at /date");
        stage.setStagedJob(job);
        val res = new DateFormat().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void quarterPastTenShouldBeOk() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setDate("11/09/2018");
        stage.setStagedJob(job);
        val res = new DateFormat().valid(stage);
        assertTrue(res.isValid());

    }
}
