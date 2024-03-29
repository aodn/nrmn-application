package au.org.aodn.nrmn.restapi.validation.data;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;

@ExtendWith(MockitoExtension.class)
class RLSDepthValidationTest {
    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    DataValidation dataValidation;

    @Test
    void depthWithMulipleDecimalsShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDepth("3.14");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Depth is invalid, expected: depth[.surveyNum]")));
    }

    @Test
    void nullDepthShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDepth(null);
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Depth is invalid, expected: depth[.surveyNum]")));
    }
}
