package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionalPositiveIntegerFormatTest {

    @Test
    void negativeValueShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setVis("-99");
        stage.setStagedJob(job);
        // val res = new OptionalPositiveIntegerFormatValidation(StagedRow::getVis, "Vis").valid(stage);
        // assertTrue(res.isInvalid());
    }

}
