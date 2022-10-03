package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.MeasurementValidation;
import au.org.aodn.nrmn.restapi.service.validation.SiteValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;

@ExtendWith(MockitoExtension.class)
class BeforeDateCheckTest {

    @InjectMocks
    ValidationProcess validationProcess;

    @Mock
    MeasurementValidation measurementValidation;
    
    @Mock
    SiteValidation siteValidation;

    @InjectMocks
    SurveyValidation surveyValidation;

    @Test
    void beforeDateShouldSucceed() throws Exception {

        StagedJob job = new StagedJob();
        job.setId(1L);

        StagedRow row = new StagedRow();
        row.setId(1L);
        row.setStagedJob(job);

        StagedRowFormatted rowFormatted = new StagedRowFormatted();
        rowFormatted.setDate(LocalDate.parse("1990-01-01"));
        rowFormatted.setRef(row);
        var error = surveyValidation.validateDateRange(ProgramValidation.ATRC, rowFormatted);
        assertTrue(error.getMessage().contains("Date must be after"));
    }

    @Test
    void afterDateShouldFail() throws Exception {
        StagedJob job = new StagedJob();
        job.setId(1L);

        StagedRow row = new StagedRow();
        row.setId(1L);
        row.setStagedJob(job);

        StagedRowFormatted rowFormatted = new StagedRowFormatted();
        rowFormatted.setDate(LocalDate.parse("2100-01-01"));
        rowFormatted.setRef(row);
        var error = surveyValidation.validateDateRange(ProgramValidation.ATRC, rowFormatted);
        assertTrue(error.getMessage().contains("Date is in the future"));
    }

    @Test
    void dateInRangeShouldSuccess() {
        StagedJob job = new StagedJob();
        job.setId(1L);

        StagedRow row = new StagedRow();
        row.setId(1L);
        row.setStagedJob(job);

        StagedRowFormatted rowFormatted = new StagedRowFormatted();
        rowFormatted.setDate(LocalDate.parse("1991-01-01"));
        rowFormatted.setRef(row);
        var error = surveyValidation.validateDateRange(ProgramValidation.ATRC, rowFormatted);
        assertNull(error);
    }
}
