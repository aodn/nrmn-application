package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class DiverExistsIT {

    @Autowired
    ValidationProcess validationProcess;

    @Autowired
    DiverRepository diverRepo;

    @Test
    void notFoundDiverShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDiver("NOP");
        row.setStagedJob(job);
        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(row));
        assertTrue(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }

    @Test
    void existingDiverShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setDiver("JEP");
        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(row));
        assertFalse(res.stream().anyMatch(e -> e.getColumnNames().contains("diver") && e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }

    @Test
    void diverNameWithAccentShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setDiver("Juán Español Página");
        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(row));
        assertFalse(res.stream().anyMatch(e -> e.getColumnNames().contains("diver") && e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }
}
