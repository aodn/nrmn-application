package au.org.aodn.nrmn.restapi.validation.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertTrue;


@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
class SiteCodeExistsIT {

    @Autowired
    SiteCodeExists siteCodeExists;

    @Test
    void notFoundSiteCodeShouldFail() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setSiteNo("xyz");
        stage.setStagedJob(job);
        val codeFound = siteCodeExists.valid(stage);
        Assertions.assertTrue(codeFound.isInvalid());
    }


    @Test
    void existingSiteCodeShouldBeOk() {
        val job = new StagedJob();
        job.setId(1);
        val stage = new StagedRow();
        stage.setSiteNo("EYR71");
        stage.setStagedJob(job);
        val codeFound = siteCodeExists.valid(stage);
        Assertions.assertTrue(codeFound.isValid());
    }
}