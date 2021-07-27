package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.SerializationUtils;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class ATRCSurveyGroupCompleteIT {
    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    StagedJobRepository jobRepo;

    // @Autowired
    // ATRCSurveyGroupComplete atrcSurveyGroupComplete;

    @Test
    void groupWithAllSurveyNumsShouldSucceed() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val siteNo = "ERZ1";

        val sn1 = new StagedRow();
        sn1.setDate(date);
        sn1.setDepth("7.1");
        sn1.setBlock("1");
        sn1.setMethod("2");
        sn1.setSiteCode(siteNo);
        sn1.setStagedJob(job);

        val sn2 = (StagedRow) SerializationUtils.clone(sn1);
        sn2.setDepth("7.2");
        val sn3 = (StagedRow) SerializationUtils.clone(sn1);
        sn3.setDepth("7.3");

        val sn4 = (StagedRow) SerializationUtils.clone(sn1);
        sn4.setDepth("7.4");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2, sn3, sn4));

        // val res = atrcSurveyGroupComplete.valid(job);

        // assertTrue(res.isValid());
    }

    @Test
    void groupWithIncompleteSurveyNumsShouldFail() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val siteNo = "ERZ1";

        val sn1 = new StagedRow();
        sn1.setDate(date);
        sn1.setDepth("7.1");
        sn1.setBlock("1");
        sn1.setMethod("2");
        sn1.setSiteCode(siteNo);
        sn1.setStagedJob(job);

        val sn2 = (StagedRow) SerializationUtils.clone(sn1);
        sn2.setDepth("7.2");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2));

        // val res = atrcSurveyGroupComplete.valid(job);

        // assertTrue(res.isInvalid());
    }

    @Test
    void groupWithCompleteSurveyBlocksShouldSucceed() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val siteNo = "ERZ1";

        val sn1b1 = new StagedRow();
        sn1b1.setDate(date);
        sn1b1.setDepth("7.1");
        sn1b1.setMethod("1");
        sn1b1.setBlock("1");
        sn1b1.setSiteCode(siteNo);
        sn1b1.setStagedJob(job);

        val sn1b2 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn1b2.setBlock("2");

        val sn2b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn2b1.setDepth("7.2");
        val sn2b2 = (StagedRow) SerializationUtils.clone(sn2b1);
        sn2b2.setBlock("2");

        val sn3b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn3b1.setDepth("7.3");
        val sn3b2 = (StagedRow) SerializationUtils.clone(sn3b1);
        sn3b2.setBlock("2");

        val sn4b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn4b1.setDepth("7.4");
        val sn4b2 = (StagedRow) SerializationUtils.clone(sn4b1);
        sn4b2.setBlock("2");

        stagedRowRepo.saveAll(Arrays.asList(sn1b1, sn1b2, sn2b1, sn2b2, sn3b1, sn3b2, sn4b1, sn4b2));

        // val res = atrcSurveyGroupComplete.valid(job);

        // assertTrue(res.isValid());
    }

    @Test
    void groupWithIncompleteSurveyBlocksShouldFail() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val siteNo = "ERZ1";

        val sn1b1 = new StagedRow();
        sn1b1.setDate(date);
        sn1b1.setDepth("7.1");
        sn1b1.setMethod("1");
        sn1b1.setBlock("1");
        sn1b1.setSiteCode(siteNo);
        sn1b1.setStagedJob(job);

        val sn1b2 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn1b2.setBlock("2");

        val sn2b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn2b1.setDepth("7.2");
        val sn2b2 = (StagedRow) SerializationUtils.clone(sn2b1);
        sn2b2.setBlock("2");

        val sn3b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn3b1.setDepth("7.3");
        val sn3b2 = (StagedRow) SerializationUtils.clone(sn3b1);
        sn3b2.setBlock("2");

        // Incomplete - missing block 2
        val sn4b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn4b1.setDepth("7.4");

        stagedRowRepo.saveAll(Arrays.asList(sn1b1, sn1b2, sn2b1, sn2b2, sn3b1, sn3b2, sn4b1));

        // val res = atrcSurveyGroupComplete.valid(job);

        // assertTrue(res.isInvalid());
    }

}
