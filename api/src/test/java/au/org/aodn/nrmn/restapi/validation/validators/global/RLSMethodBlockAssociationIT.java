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

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@ActiveProfiles("cicd")
class RLSMethodBlockAssociationIT {

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    RLSMethodBlockAssociation blockMethodCheck;

    @Test
    void expectedAssociationShouldSuccess() {
        stagedRowRepo.deleteAll();
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

        val m1b2 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        val m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");

        val m2b2 = (StagedRow) SerializationUtils.clone(m2b1);
        m2b2.setBlock("2");


        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1, m2b2));

        val res = blockMethodCheck.valid(job);
        assertTrue(res.isValid());
    }
    @Test
    void missingOnBlockShouldFail() {
        stagedRowRepo.deleteAll();

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

        val m1b2 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        val m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1));

        val res = blockMethodCheck.valid(job);
        assertTrue(res.isInvalid());
    }

}