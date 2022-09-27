package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;

@ExtendWith(MockitoExtension.class)
class Block0DataCheckTest {

    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    ValidationProcess validationProcess;

    @Test
    void block0MethodOutOfRangeShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setBlock("0");
        row.setMethod("2");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals("Block 0 is invalid for method")));
    }

    @Test
    void block0MethodInRangeShouldSucceed() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setBlock("0");
        row.setMethod("5");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().equals("Block 0 is invalid for method")));
    }
}
