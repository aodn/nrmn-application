package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatTest {
    @Test
    void incorrectDateFormatShouldFail() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setDate("not /at /date");
        stage.setStagedJob(job);
        val res = new DateFormat().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void quarterPastTenShouldBeOk() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setDate("11/09/2018");
        stage.setStagedJob(job);
        val res = new DateFormat().valid(stage);
        assertTrue(res.isValid());

    }
}
