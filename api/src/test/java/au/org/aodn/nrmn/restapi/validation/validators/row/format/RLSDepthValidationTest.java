package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RLSDepthValidationTest {

    @Test
    void depthWithMulipleDecimalsShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("3.14");
        stage.setStagedJob(job);
        val res =
                new RLSDepthValidation().valid(stage);
        assertTrue(res.isInvalid());

    }
}
