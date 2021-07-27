package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatTest {
    @Test
    void incorrectDateFormatShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDate("not /at /date");
        stage.setStagedJob(job);
        // val res = new DateFormatValidation().valid(stage);
        // assertTrue(res.isInvalid());

    }

    @Test
    void longDateFormatShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDate("11/09/2018");
        stage.setStagedJob(job);
        // val res = new DateFormatValidation().valid(stage);
        // assertTrue(res.isValid());

    }

    @Test
    void shortDateFormatShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDate("11/9/18");
        stage.setStagedJob(job);
        // val res = new DateFormatValidation().valid(stage);
        // assertTrue(res.isValid());

    }
}
