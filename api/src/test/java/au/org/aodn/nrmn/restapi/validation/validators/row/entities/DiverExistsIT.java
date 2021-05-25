package au.org.aodn.nrmn.restapi.validation.validators.row.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import au.org.aodn.nrmn.restapi.validation.validators.row.entities.DiverExists;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;
@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class DiverExistsIT {

    @Autowired
    DiverRepository diverRepo;

    @Test
    void notFoundDiverShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDiver("NOP");
        stage.setStagedJob(job);
        val diverFound = new DiverExists(StagedRow::getDiver, "Diver", diverRepo, ValidationLevel.BLOCKING).valid(stage);
        assertTrue(diverFound.isInvalid());
    }

    @Test
    void existingDiverShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setStagedJob(job);
        stage.setDiver("TJR");
        val diverFound = new DiverExists(StagedRow::getDiver, "Diver", diverRepo, ValidationLevel.BLOCKING).valid(stage);
        assertTrue(diverFound.isValid());
    }
}
