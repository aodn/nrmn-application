package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.test.PostgresqlContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@ActiveProfiles("cicd")
class SpeciesExistsIT {
    @Autowired
    SpeciesExists speciesExists;

    @Test
    void notFoundSpeciesCodeShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSpecies("Species 20");
        stage.setStagedJob(job);
        val codeFound = speciesExists.valid(stage);
        Assertions.assertTrue(codeFound.isInvalid());
    }

    @Test
    void existingSiteCodeShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSpecies("Specie 56");
        stage.setStagedJob(job);
        val codeFound = speciesExists.valid(stage);
        Assertions.assertTrue(codeFound.isValid());
    }
}
