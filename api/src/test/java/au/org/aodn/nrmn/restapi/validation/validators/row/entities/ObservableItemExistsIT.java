package au.org.aodn.nrmn.restapi.validation.validators.row.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import au.org.aodn.nrmn.restapi.validation.validators.row.entities.ObservableItemExists;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class ObservableItemExistsIT {
    @Autowired
    ObservableItemExists observableItemExists;

    @Test
    void notFoundSpeciesShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSpecies("Species 20");
        stage.setStagedJob(job);
        val codeFound = observableItemExists.valid(stage);
        Assertions.assertTrue(codeFound.isInvalid());
    }

    @Test
    void existingSpeciesShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSpecies("Specie 56");
        stage.setStagedJob(job);
        val codeFound = observableItemExists.valid(stage);
        Assertions.assertTrue(codeFound.isValid());
    }

    @Test
    void surveyNotFoundShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSpecies("Survey not done");
        stage.setStagedJob(job);
        val codeFound = observableItemExists.valid(stage);
        Assertions.assertTrue(codeFound.isValid());
    }
}
