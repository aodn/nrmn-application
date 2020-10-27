package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
                stage.setLmax("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormat(StagedSurveyEntity::getLmax, "Lmax").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSuccess() {
        val job = new StagedJobEntity();
        job.setId("idJob");
        val stage = new StagedSurveyEntity();
        stage.setLmax("10");
        stage.setStagedJob(job);
        val res = new IntegerFormat(StagedSurveyEntity::getLmax, "Lmax").valid(stage);
        assertTrue(res.isValid());
    }


}