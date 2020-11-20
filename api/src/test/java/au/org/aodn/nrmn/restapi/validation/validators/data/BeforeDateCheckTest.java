package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;

class BeforeDateCheckTest {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Test
    void beforeDateShouldSucceed() throws Exception {
        val beforeDate = LocalDate.parse("11/02/1993", dtf);
        val stage = new StagedRowFormatted();
        stage.setDate( LocalDate.parse("01/01/1994", dtf));
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void afterDateShouldFail() throws Exception {
        val beforeDate = LocalDate.parse("02/01/1994", dtf);
        val stage = new StagedRowFormatted();
        stage.setDate(LocalDate.parse("01/01/1994", dtf));
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isInvalid());
    }
}
