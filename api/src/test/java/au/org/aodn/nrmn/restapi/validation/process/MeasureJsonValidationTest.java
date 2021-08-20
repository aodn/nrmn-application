package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;

@ExtendWith(MockitoExtension.class)
class MeasureJsonValidationTest {

    @InjectMocks
    ValidationProcess validationProcess;

    
    @Mock
    DiverRepository diverRepository;
    
    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

     @Test
    void measureJsonWithIntShouldSuccess() {

        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setMeasureJson(
                new HashMap<Integer, String>() {
                    {
                        put(2, "5");    // VALID
                        put(7, "10");   // VALID
                        put(9, "39");   // VALID
                    }
                }
        );
        row.setStagedJob(job);
        Collection<ValidationError> res = validationProcess.checkFormatting("ATRC", false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(row));
        assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Not a valid measurement")));
    }

    @Test
    void measureJsonWithNaNtShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setMeasureJson(
                new HashMap<Integer, String>() {
                    {
                        put(2, "5");        // VALID
                        put(7, "hey");      // INVALID
                        put(9, "----");     // INVALID
                    }
                }
        );
        row.setStagedJob(job);
        Collection<ValidationError> res = validationProcess.checkFormatting("ATRC", false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(row));
        assertTrue(res.stream().filter(e -> e.getMessage().equalsIgnoreCase("Not a valid measurement")).count() == 2);
    }
}
