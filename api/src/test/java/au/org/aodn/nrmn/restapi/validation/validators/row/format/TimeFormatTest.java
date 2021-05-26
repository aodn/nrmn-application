package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeFormatTest {

    @Test
    void incorrectTimeFormatShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTime("ti:me");
        stage.setStagedJob(job);
        val res = new TimeFormatValidation().valid(stage);
        assertTrue(res.isInvalid());

    }

    @Test
    void quarterPastTenShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTime("10:15");
        stage.setStagedJob(job);
        val res = new TimeFormatValidation().valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void beyoundBoundaryShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTime("40:15");
        stage.setStagedJob(job);
        val res = new TimeFormatValidation().valid(stage);
        assertTrue(res.isInvalid());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "10:15",
            "22:15",
            "10:15 am",
            "10:15 pm",
            "10:15 AM",
            "10:15 PM",
            "10:15:23",
            "22:15:23",
            "10:15:23 am",
            "10:15:23 pm",
            "10:15:23 AM",
            "10:15:23 PM"
    })
    void differentFormatsShouldBeOk(String value) {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTime(value);
        stage.setStagedJob(job);
        val res = new TimeFormatValidation().valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void missingTimeShouldBeOK() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTime("");
        stage.setStagedJob(job);
        val res = new TimeFormatValidation().valid(stage);
        assertTrue(res.isValid());
    }


}
