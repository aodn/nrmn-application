package au.org.aodn.nrmn.restapi.validation.validators.row.data;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import lombok.val;

class BeforeDateCheckTest {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    void beforeDateShouldSucceed() throws Exception {
        val beforeDate = LocalDate.parse("11/02/1993", dtf);
        val job = new StagedJob();
        job.setId(1L);

        val row = new StagedRow();
        row.setId(1L);
        row.setStagedJob(job);

        val stage = new StagedRowFormatted();
        stage.setDate( LocalDate.parse("01/01/1994", dtf));
        stage.setRef(row);
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void afterDateShouldFail() throws Exception {
        val job = new StagedJob();
        job.setId(1L);

        val row = new StagedRow();
        row.setId(1L);
        row.setStagedJob(job);

        val beforeDate = LocalDate.parse("02/01/1994", dtf);
        val stage = new StagedRowFormatted();
        stage.setRef(row);
        stage.setDate(LocalDate.parse("01/01/1994", dtf));
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isInvalid());
    }
}
