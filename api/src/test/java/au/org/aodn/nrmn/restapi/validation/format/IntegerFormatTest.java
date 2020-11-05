package au.org.aodn.nrmn.restapi.validation.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
                stage.setLmax("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormat(StagedRow::getLmax, "Lmax", Collections.emptyList()).valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSuccess() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setLmax("10");
        stage.setStagedJob(job);
        val res = new IntegerFormat(StagedRow::getLmax, "Lmax", Collections.emptyList()).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void withinCategoryShouldSuccess() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormat(StagedRow::getMethod, "Lmax", Stream.of(1,2,3,4,7,8).collect(Collectors.toList())).valid(stage);

        assertTrue(res.isValid());
    }
}