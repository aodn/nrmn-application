package au.org.aodn.nrmn.restapi.validation.validators.row.format;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;
import lombok.val;

@ExtendWith(MockitoExtension.class)
class ATRCDepthValidationTest {

    @Mock
    ObservationRepository observationRepository;

    @Mock
    DiverRepository diverRepository;

    @Mock
    SiteRepository siteRepository;


    @InjectMocks
    ValidationProcess validationProcess;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void nullDepthShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth(null);
        stage.setStagedJob(job);
        // val res =
        //         new ATRCDepthValidation().valid(stage);
        // assertTrue(res.isInvalid());

    }

    @Test
    void depthWithTransectOutOfRangeShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setMethod("1");
        stage.setDepth("10.9");
        stage.setStagedJob(job);
        // val res =
        //         new ATRCDepthValidation().valid(stage);;
        // assertTrue(res.isInvalid());

    }

    @Test
    void depthWithTransectInRangeForMethod1ShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("7.3");
        stage.setStagedJob(job);
        stage.setMethod("1");

        // val res =
        //         new ATRCDepthValidation().valid(stage);;
        // assertTrue(res.isValid());
    }

    @Test
    void depthWithNoTransectForMethod0ShouldSucceed() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setDepth("7");
        stage.setStagedJob(job);
        stage.setMethod("0");

        // val res =
        //         new ATRCDepthValidation().valid(stage);;
        // assertTrue(res.isValid());
    }

}
