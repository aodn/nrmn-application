package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanFormatTest {

    @Test
    void stringNoisValid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("no");
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isValid());
        boolean validatedValue = res.fold(null,r->r);
        assertFalse(validatedValue);
    }

    @Test
    void stringYesisValid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("yes");
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isValid());
        boolean validatedValue = res.fold(null,r->r);
        assertTrue(validatedValue);
    }

    @Test
    void stringTrueIsInvalid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("True");
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void stringFalseIsInvalid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("false");
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void nullStringIsInvalid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing(null);
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isInvalid());
    }

    @Test
    void emptyStringIsInvalid() {
        val job = new StagedJob();
        job.setReference("idJob");
        val stage = new StagedRow();
        stage.setIsInvertSizing("");
        stage.setStagedJob(job);
        val res = new BooleanFormatValidation(StagedRow::getIsInvertSizing, "sInvertSizing").valid(stage);
        assertTrue(res.isInvalid());
    }
}
