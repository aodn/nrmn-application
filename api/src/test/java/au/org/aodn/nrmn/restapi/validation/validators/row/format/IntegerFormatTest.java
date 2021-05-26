package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;

import static au.org.aodn.nrmn.restapi.util.ValidatorHelpers.toErrorList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerFormatTest {

    @Test
    void nanShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
                stage.setTotal("Not a number");
                stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getTotal, "Total", Collections.emptyList()).valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void tenShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setTotal("10");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getTotal, "Total", Collections.emptyList()).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void withinCategoryShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Total", Stream.of(1,2,3,4,7,8).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isValid());
    }

    @Test
    void singleValidValueErrorMessage() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Total", Stream.of(8).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isInvalid());
        assertEquals("[7] is invalid. Must be 8", toErrorList(res).get(0).getId().getMessage());
    }

    @Test
    void multipleValidValueErrorMessage() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("7");
        stage.setStagedJob(job);
        val res = new IntegerFormatValidation(StagedRow::getMethod, "Total",
         Stream.of(8, 9, 10).collect(Collectors.toList())).valid(stage);
        assertTrue(res.isInvalid());
        assertEquals("[7] is invalid. Must be 8, 9 or 10", toErrorList(res).get(0).getId().getMessage());
    }
}
