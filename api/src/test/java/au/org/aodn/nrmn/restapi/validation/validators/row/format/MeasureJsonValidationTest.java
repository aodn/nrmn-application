package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MeasureJsonValidationTest {
    @Test
    void measureJsonWithIntShouldSuccess() {

        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMeasureJson(
                new HashMap<Integer, String>() {
                    {
                        put(2, "5");
                        put(7, "10");
                        put(9, "39");
                    }
                }
        );
        stage.setStagedJob(job);

        val res = new MeasureJsonValidation().valid(stage);

        assertTrue(res.isValid());

        val hashMap = res.orElseGet( null);
        assertEquals(hashMap.get(2), 5);
        assertEquals(hashMap.get(7), 10);
        assertEquals(hashMap.get(9), 39);


    }

    @Test
    void measureJsonWithNaNtShouldFail() {

        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMeasureJson(
                new HashMap<Integer, String>() {
                    {
                        put(2, "5");
                        put(7, "hey");
                        put(9, "--");
                    }
                }
        );
        stage.setStagedJob(job);

        val res = new MeasureJsonValidation().valid(stage);

        assertTrue(res.isInvalid());
    }



}
