package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class ObservableItemExistsIT {

    @Autowired
    ValidationProcess validationProcess;

    @Test
    void notFoundSpeciesShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val row = new StagedRow();
        row.setSpecies("Species 20");
        row.setStagedJob(job);
        Collection<ValidationError> errors = validationProcess.checkFormatting("ATRC", false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }

    @Test
    void existingSpeciesShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val row = new StagedRow();
        row.setSpecies("Specie 56");
        row.setStagedJob(job);
        Collection<ValidationError> errors = validationProcess.checkFormatting("ATRC", false, Arrays.asList(), Arrays.asList(ObservableItem.builder().observableItemName("Specie 56").build()), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }

    @Test
    void surveyNotFoundShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val row = new StagedRow();
        row.setSpecies("Survey not done");
        row.setStagedJob(job);
        Collection<ValidationError> errors = validationProcess.checkFormatting("ATRC", false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }
}
