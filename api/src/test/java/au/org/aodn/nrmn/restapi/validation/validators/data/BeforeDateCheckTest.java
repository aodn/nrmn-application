package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.validators.data.BeforeDateCheck;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

class BeforeDateCheckTest {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    void beforeDateShouldSuccess() throws Exception{
        val beforeDate = sdf.parse("11/02/1993");
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setDate("01/01/1994");
        stage.setStagedJob(job);
       val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void afterDateShouldFail() throws Exception {
        val beforeDate = sdf.parse("02/01/1994");
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setDate("01/01/1994");
        stage.setStagedJob(job);
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isInvalid());
    }
}