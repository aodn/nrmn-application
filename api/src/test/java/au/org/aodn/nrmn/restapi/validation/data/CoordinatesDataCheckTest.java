package au.org.aodn.nrmn.restapi.validation.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesDataCheckTest extends ValidatorHelpers {

    @Test
    void latLongCorrectShouldBeOk() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setStagedJob(job);
        stage.setLatitude("48.8566");
        stage.setLongitude("2.3522");
        val res = new CoordinatesDataCheck().valid(stage);
        assertTrue(res.isValid());

    }


    @Test
    void latIncorrectShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setStagedJob(job);
        stage.setLatitude("90.8566");
        stage.setLongitude("2.3522");
        val res = new CoordinatesDataCheck().valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.get(0).getId().getMessage(), "Latitude is not between -90.0 and 90.0");

    }

    @Test
    void longIncorrectShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setStagedJob(job);
        stage.setLatitude("50.8566");
        stage.setLongitude("-192.3522");
        val res = new CoordinatesDataCheck().valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.get(0).getId().getMessage(), "Longitude is not between -180.0 and 180.0");

    }
    @Test
    void BothIncorrectShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setStagedJob(job);
        stage.setLatitude("-90.8566");
        stage.setLongitude("-192.3522");
        val res = new CoordinatesDataCheck().valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.size(),2);

    }
}