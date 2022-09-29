package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class DuplicateRowCheckTest {

    @InjectMocks
    ValidationProcess validationProcess;

    
    @Mock
    DiverRepository diverRepository;
    
    @Test
    void duplicateRowShouldFail() {
        StagedRow r1 = StagedRow.builder().block("1").pos(1).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();
        StagedRow r2 = StagedRow.builder().block("1").pos(2).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();
        StagedRow r3 = StagedRow.builder().block("1").pos(3).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();

        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(r1, r2, r3));
        assertTrue(res.stream().anyMatch(e -> e.getLevelId() == ValidationLevel.DUPLICATE));
    }

    @Test
    void duplicateRowValidationShouldIgnoreMeasurements() {
        StagedRow r1 = StagedRow.builder().block("1").pos(1).measureJson(new HashMap<Integer, String>() {{
            put(1, "35");
            put(2, "421");
        }}).build();
        StagedRow r2 = StagedRow.builder().block("1").pos(2).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
            put(4, "7");
            put(5, "999");
        }}).build();
        StagedRow r3 = StagedRow.builder().block("1").pos(3).measureJson(new HashMap<Integer, String>() {{
            put(3, "3");
            put(5, "47");
        }}).build();

        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(r1, r2, r3));
        assertTrue(res.stream().anyMatch(e -> e.getLevelId() == ValidationLevel.DUPLICATE));
    }

    @Test
    void nonDuplicatedRowsShouldSucceed() {
        StagedRow r1 = StagedRow.builder().block("1").pos(1).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();
        StagedRow r2 = StagedRow.builder().block("2").pos(2).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();
        StagedRow r3 = StagedRow.builder().block("4").pos(3).measureJson(new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }}).build();

        Collection<SurveyValidationError> res = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(r1, r2, r3));
        assertFalse(res.stream().anyMatch(e -> e.getLevelId() == ValidationLevel.DUPLICATE));
    }
}
