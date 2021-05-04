package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import lombok.val;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SpeciesNotFoundCheckTest {

    @Test
    void  outOfScopeShouldSuccess( ) {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setSpecies("Species 50");
        stage.setMeasureJson(ImmutableMap.<Integer, String>builder().put(1, "2").build());
        val validators =  new SpeciesNotFoundCheck();
        val res =  validators.valid(stage);
        assertTrue(res.isValid());
    }
    @Test
    void  emptyMeasureShouldSuccess( ) {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setSpecies("No Species Found");
        stage.setMeasureJson(ImmutableMap.<Integer, String>builder().put(1, "0").build());
       val validators =  new SpeciesNotFoundCheck();
       val res =  validators.valid(stage);
       assertTrue(res.isValid());
    }

    @Test
    void  withMeasuresShouldFail( ) {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setSpecies("No Species Found");
        stage.setMeasureJson(ImmutableMap.<Integer, String>builder().put(1, "2").put(2, "0").build());
        val validators =  new SpeciesNotFoundCheck();
        val res =  validators.valid(stage);
        assertTrue(res.isInvalid());
    }
    @Test
    void  emptyMapMeasureShouldSuccess( ) {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setSpecies("No Species Found");
        stage.setMeasureJson(Collections.emptyMap());
        val validators =  new SpeciesNotFoundCheck();
        val res =  validators.valid(stage);
        assertTrue(res.isValid());
    }
}
