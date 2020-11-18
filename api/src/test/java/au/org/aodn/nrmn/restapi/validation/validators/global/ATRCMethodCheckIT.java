package au.org.aodn.nrmn.restapi.validation.validators.global;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.test.PostgresqlContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.SerializationUtils;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@ActiveProfiles("cicd")
class ATRCMethodCheckIT {
    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    StagedJobRepository jobRepo;
    @Autowired
    ATRCMethodCheck atrcMethodCheck;

    @Test
    void only12methodShouldSucceed() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val depth = "7";
        val siteNo = "ERZ1";

        val m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteNo(siteNo);
        m1b1.setStagedJob(job);

        val m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        val m2b2 = (StagedRow) SerializationUtils.clone(m2b1);
        m2b1.setBlock("2");

        val m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        val m2b1d8 = (StagedRow) SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m2b1, m2b2, m1b1d8, m2b1d8));

        val res = atrcMethodCheck.valid(job);

        assertTrue(res.isValid());
    }


    @Test
    void onlyMethod0345ShouldSucceed() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val depth = "7";
        val siteNo = "ERZ1";

        val m0b1 = new StagedRow();
        m0b1.setMethod("0");
        m0b1.setBlock("1");
        m0b1.setDate(date);
        m0b1.setDepth(depth);
        m0b1.setSiteNo(siteNo);
        m0b1.setStagedJob(job);

        val m0b2 = (StagedRow) SerializationUtils.clone(m0b1);
        m0b2.setBlock("2");

        val m3b1 = (StagedRow) SerializationUtils.clone(m0b1);
        m3b1.setMethod("3");

        val m3b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m3b3.setBlock("3");


        val m5b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m3b3.setBlock("5");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(m0b1, m0b2, m3b1, m3b3, m5b3));

        val res = atrcMethodCheck.valid(job);

        assertTrue(res.isValid());
    }

    @Test
    void missingM2ShouldFail() {
        val job = jobRepo.findByReference("jobid-atrc").get();
        val date = "11/09/2020";
        val depth = "7";
        val siteNo = "ERZ1";

        val m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteNo(siteNo);
        m1b1.setStagedJob(job);

        val m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        val m2b1d8 = (StagedRow) SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b1d8, m2b1d8));

        val res = atrcMethodCheck.valid(job);

        assertTrue(res.isInvalid());
    }
}
