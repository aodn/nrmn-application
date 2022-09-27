package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationCell;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class ValidationProcessTest {
    
    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;

    @InjectMocks
    ValidationProcess validationProcess;

    @InjectMocks
    MeasurementValidation measurementValidation;

    @Test
    void incorrectTimeFormatShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTime("ti:me");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Time format is not valid")));
    }

    @Test
    void quarterPastTenShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTime("10:15");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Time format is not valid")));
    }

    @Test
    void invalidTimeShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTime("40:15");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Time format is not valid")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "10:15",
            "22:15",
            "10:15 am",
            "10:15 pm",
            "10:15 AM",
            "10:15 PM",
            "10:15:23",
            "22:15:23",
            "10:15:23 am",
            "10:15:23 pm",
            "10:15:23 AM",
            "10:15:23 PM"
    })
    void differentFormatsShouldBeOk(String value) {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTime(value);
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Time format is not valid")));
    }

    @Test
    void missingTimeShouldBeOK() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTime("");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Time format is not valid")));
    }

    @Test
    void negativeValueShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setVis("-99");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Vis is not positive")));
    }

    @Test
    void missingValueShouldSucceed() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setVis("");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Vis")));
    }

    @Test
    void nanShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTotal("Not a number");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Total is not an integer")));
    }

    @Test
    void tenShouldSucceed() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setTotal("10");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Total is not an integer")));
    }

    @Test
    void minusValueShouldBeOK() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setLongitude("-67.192519");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Longitude")));
    }

    
    @Test
    void incorrectDateFormatShouldFail() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDate("not /at /date");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Date format is not valid")));
    }

    @Test
    void longDateFormatShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDate("11/09/2018");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Date format is not valid")));
    }

    @Test
    void shortDateFormatShouldBeOk() {
        StagedJob job = new StagedJob();
        job.setId(1L);
        StagedRow row = new StagedRow();
        row.setDate("11/9/18");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Date format is not valid")));
    }
    
    
    @Test
    void stringNoisValid() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("no");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("must be 'Yes' or 'No'")));
    }

    @Test
    void stringYesisValid() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("yes");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("must be 'Yes' or 'No'")));
    }

    @Test
    void stringTrueIsInvalid() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        job.setIsExtendedSize(true);
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("True");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("must be 'Yes' or 'No'")));
    }

    @Test
    void invalidIsInvertIsInvalidForExtended() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        job.setIsExtendedSize(true);
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("false");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("must be 'Yes' or 'No'")));
    }

    @Test
    void emptyIsInvertIsInvalidForExtended() {
        StagedJob job = new StagedJob();
        job.setReference("idJob");
        job.setIsExtendedSize(true);
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Use Invert Sizing is blank")));
    }

    @Test
    void emptyStringIsValidForNotExtended() {
        StagedJob job = new StagedJob();
        job.setIsExtendedSize(false);
        StagedRow row = new StagedRow();
        row.setIsInvertSizing("");
        row.setStagedJob(job);
        Collection<SurveyValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, job.getIsExtendedSize(), Arrays.asList(), Arrays.asList(), Arrays.asList(row));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("must be 'Yes' or 'No'")));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().contains("Use Invert Sizing is blank")));
    }
    /**
     *  Calculated total issue is a blocking error not warning
     */
    @Test
    void calculatedTotalIsAnError() {
        StagedRowFormatted stagedRowFormatted = StagedRowFormatted
                .builder()
                .measureJson(new HashMap<>())
                .total(1)
                .code("NAT")        // Some code that will not trigger Debris zero blocking
                .build();

        stagedRowFormatted.getMeasureJson().put(1, 10);
        stagedRowFormatted.getMeasureJson().put(2, 11);

        Collection<ValidationCell> errors = measurementValidation.validateMeasurements(ProgramValidation.NONE, stagedRowFormatted);
        assertTrue(errors.stream().filter(f -> f.getLevelId() == ValidationLevel.BLOCKING && f.getMessage().contains("Calculated total is 21")).findAny().isPresent(),
                "BLOCKING error for total mismatch");
    }
}
