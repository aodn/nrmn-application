package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleFormatTest {
    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setLongitude("Not a number");
        stage.setStagedJob(job);
        val res = new DoubleFormat(StagedSurvey::getLongitude, "Longitude").valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void minusValueShouldBeOK(){
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setLongitude("-67.192519");
        stage.setStagedJob(job);
        val res = new DoubleFormat(StagedSurvey::getLongitude, "Longitude").valid(stage);
        assertTrue(res.isValid());


    }

}