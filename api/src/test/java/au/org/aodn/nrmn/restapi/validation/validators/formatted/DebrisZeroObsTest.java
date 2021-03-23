package au.org.aodn.nrmn.restapi.validation.validators.formatted;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DebrisZeroObsTest {

    @Test
    public void dezDebrizwith0ShouldbeOK() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setCode("dez");
        stage.setSpecies("Debris");
        stage.setTotal("0");
        stage.setInverts("0");
        val measure = new HashMap<Integer, String>();
        stage.setMeasureJson(measure);
        stage.setStagedJob(job);
    }
}
