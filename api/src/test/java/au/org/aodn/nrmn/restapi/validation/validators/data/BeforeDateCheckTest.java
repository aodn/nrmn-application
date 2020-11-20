package au.org.aodn.nrmn.restapi.validation.validators.data;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.data.BeforeDateCheck;
import com.amazonaws.services.medialive.model.AacRawFormat;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

class BeforeDateCheckTest {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    void beforeDateShouldSucceed() throws Exception {
        val beforeDate = sdf.parse("11/02/1993");
        val stage = new StagedRowFormatted();
        stage.setDate(sdf.parse("01/01/1994"));
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void afterDateShouldFail() throws Exception {
        val beforeDate = sdf.parse("02/01/1994");
        val stage = new StagedRowFormatted();
        stage.setDate(sdf.parse("01/01/1994"));
        val res = new BeforeDateCheck(beforeDate).valid(stage);
        assertTrue(res.isInvalid());
    }
}
