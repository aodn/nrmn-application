package au.org.aodn.nrmn.restapi.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@WithTestData
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
class DiverExistsIT {

    @Autowired
    DataValidation dataValidation;

    @Autowired
    DiverRepository diverRepo;

    @Test
    void notFoundDiverShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDiver("NOP");
        row.setStagedJob(job);
        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"), List.of(), List.of(row));
        assertTrue(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }

    @Test
    void existingDiverShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setDiver("JEP");
        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"), List.of(), List.of(row));
        Assertions.assertFalse(res.stream().anyMatch(e -> e.getColumnNames().contains("diver") && e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }

    @Test
    void diverNameWithAccentShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setStagedJob(job);
        row.setDiver("Juán Español Página");
        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"), List.of(), List.of(row));
        Assertions.assertFalse(res.stream().anyMatch(e -> e.getColumnNames().contains("diver") && e.getMessage().equalsIgnoreCase("Diver does not exist")));
    }
}
