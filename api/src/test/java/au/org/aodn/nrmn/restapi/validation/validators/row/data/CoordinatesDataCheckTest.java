package au.org.aodn.nrmn.restapi.validation.validators.row.data;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesDataCheckTest extends ValidatorHelpers {

    @Test
    void latLongCorrectShouldBeOk() {
        Site site = new Site();
        site.setLatitude(48.8566);
        site.setLongitude(2.3522);
        site.calcGeom();
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setLatitude("48.8566");
        stage.setLongitude("2.3522");
        val res = new CoordinatesDataCheck(site).valid(stage);
        assertTrue(res.isValid());

    }


    @Test
    void notMatchingSiteShouldFail() {
        Site site = new Site();
        site.setLatitude(48.9566);
        site.setLongitude(2.3522);
        site.calcGeom();
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setLatitude("48.8566");
        stage.setLongitude("2.3522");
        val res = new CoordinatesDataCheck(site).valid(stage);
        assertTrue(res.isInvalid());

    }


    @Test
    void latIncorrectShouldFail() {
        Site site = new Site();
        site.setLatitude(48.8566);
        site.setLongitude(2.3522);
        site.calcGeom();

        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setLatitude("90.8566");
        stage.setLongitude("2.3522");
        val res = new CoordinatesDataCheck(site).valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.get(0).getId().getMessage(), "Latitude is not between -90.0 and 90.0");

    }

    @Test
    void longIncorrectShouldFail() {
        Site site = new Site();
        site.setLatitude(48.8566);
        site.setLongitude(2.3522);
        site.calcGeom();
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setLatitude("50.8566");
        stage.setLongitude("-192.3522");
        val res = new CoordinatesDataCheck(site).valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.get(0).getId().getMessage(), "Longitude is not between -180.0 and 180.0");

    }
    @Test
    void BothIncorrectShouldFail() {
        Site site = new Site();
        site.setLatitude(48.8566);
        site.setLongitude(2.3522);
        site.calcGeom();
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setLatitude("-90.8566");
        stage.setLongitude("-192.3522");
        val res = new CoordinatesDataCheck(site).valid(stage);
        val errorList = toErrorList(res);
        assertEquals(errorList.size(),2);

    }
}
