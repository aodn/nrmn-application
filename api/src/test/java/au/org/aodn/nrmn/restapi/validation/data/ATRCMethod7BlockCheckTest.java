package au.org.aodn.nrmn.restapi.validation.data;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class ATRCMethod7BlockCheckTest {

    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    DataValidation dataValidation;  

    @Test
    void method7OnBlock2ShouldSucceed() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        StagedRow row = new StagedRow();
        row.setMethod("7");
        row.setBlock("2");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("ATRC Method 7")));
    }

    @Test
    void method7OnBlock3ShouldFail() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        StagedRow row = new StagedRow();
        row.setMethod("7");
        row.setBlock("3");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("ATRC Method 7")));
    }

    @Test
    void method2OnBlock2ShouldSucceed() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        StagedRow row = new StagedRow();
        row.setMethod("2");
        row.setBlock("2");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("ATRC Method 7")));
    }

}
