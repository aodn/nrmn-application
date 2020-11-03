package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanFormatTest {


    @Test
    void booleanShouldbeOk() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setIsInvertSizing("True");
        stage.setStagedJob(job);
        val res = new BooleanFormat(StagedSurvey::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isValid());
    }

}