package au.org.aodn.nrmn.restapi.validation.data;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
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
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;

@ExtendWith(MockitoExtension.class)
class ATRCMethod11Block0Test {

    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    DataValidation dataValidation;  

    @Test
    void m11b0OnIngestShouldFail() {
        var job = new StagedJob();
        job.setReference("idJob");

        var row = new StagedRow();
        row.setMethod("11");
        row.setBlock("0");
        row.setSpecies("Haliotis rubra");
        row.setStagedJob(job);

        var errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(), Arrays.asList(), Arrays.asList(row));

        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Block 0 is invalid for method")));
    }

    @Test
    void m11b0OnIngestShouldSucceed() {
        var job = new StagedJob();
        job.setReference("idJob");

        var row = new StagedRow();
        row.setMethod("11");
        row.setBlock("0");
        row.setSpecies("Haliotis rubra");
        row.setStagedJob(job);

        var errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, false, Arrays.asList(), Arrays.asList(), Arrays.asList(row));

        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Block 0 is invalid for method")));
    }
}
