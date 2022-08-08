package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@ExtendWith(MockitoExtension.class)
class BeforeDateCheckTest {

    @InjectMocks
    ValidationProcess validationProcess;
    
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
        Collection<ValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(rowFormatted));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Date must be after")));
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
        Collection<ValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(rowFormatted));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().startsWith("Date is in the future")));
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

        Collection<ValidationError> errors = validationProcess.checkData(ProgramValidation.ATRC, false, Arrays.asList(rowFormatted));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().startsWith("Date is in the future")));
        assertFalse(errors.stream().anyMatch(e -> e.getMessage().startsWith("Date must be after")));
    }
}
