package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalBooleanFormatTest {

    @Test
    void booleanShouldbeOk() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("True");
        stage.setStagedJob(job);
        val res = new OptionalBooleanFormatValidation(StagedRow::getIsInvertSizing, "isInvertSizing").valid(stage);
        assertTrue(res.isValid());
    }
    
    @Test
    void missingValueShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setIsInvertSizing("");
        stage.setStagedJob(job);
        val res = new OptionalBooleanFormatValidation(StagedRow::getVis, "isInvertSizing").valid(stage);
        assertTrue(res.isValid());
    }

}
