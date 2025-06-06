package au.org.aodn.nrmn.restapi.integration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@Transactional
@WithTestData
class ObservableItemExistsIT {

    @Autowired
    DataValidation dataValidation;

    @Test
    void notFoundSpeciesShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setSpecies("Species 20");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of(),
                List.of(), List.of(row));
        Assertions.assertTrue(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }

    @Test
    void existingSpeciesShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setSpecies("Species 56");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of(),
                Collections.singletonList(ObservableItem.builder().observableItemName("Species 56").build()), List.of(row));
        Assertions.assertFalse(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }

    @Test
    void surveyNotFoundShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setSpecies("Survey not done");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of(),
                List.of(), List.of(row));
        Assertions.assertFalse(errors.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Species does not exist")));
    }
}
