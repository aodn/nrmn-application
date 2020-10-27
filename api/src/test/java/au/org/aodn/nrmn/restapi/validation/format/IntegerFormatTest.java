package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
                stage.setLmax("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormat(StagedSurvey::getLmax, "Lmax").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSuccess() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setLmax("10");
        stage.setStagedJob(job);
        val res = new IntegerFormat(StagedSurvey::getLmax, "Lmax").valid(stage);
        assertTrue(res.isValid());
    }


}