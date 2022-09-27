package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertTrue;

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
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class RLSDepthValidationTest {
    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    ValidationProcess validationProcess;

    @Test
    void depthWithMulipleDecimalsShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDepth("3.14");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Depth is invalid, expected: depth[.surveyNum]")));
    }

    @Test
    void nullDepthShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDepth(null);
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Depth is invalid, expected: depth[.surveyNum]")));
    }
}
