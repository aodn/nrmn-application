package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
class DiverExistsIT {

    @Autowired
    DiverExists diverExists;

    @Test
    void notFoundDiverShouldFail() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setDiver("NOP");
        stage.setStagedJob(job);
        val diverFound = diverExists.valid(stage);
        assertTrue(diverFound.isInvalid());
    }

    @Test
    void existingDiverShouldBeOk() {
        val job = new StagedJob();
        job.setId("idJob");
        val stage = new StagedSurvey();
        stage.setStagedJob(job);
        stage.setDiver("TJR");
        val diverFound = diverExists.valid(stage);
        assertTrue(diverFound.isValid());
    }



}