package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeFormatTest {

    @Test
    void incorrectTimeFormatShouldFail() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setTime("ti:me");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void quarterPastTenShouldBeOk() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setTime("10:15");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isValid());

    }

    @Test
    void beyoundBoundaryShouldFail() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setTime("40:15");
        stage.setStagedJob(job);
        val res = new TimeFormat().valid(stage);
        assertTrue(res.isInvalid());
    }
}