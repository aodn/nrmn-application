package au.org.aodn.nrmn.restapi.validation.validators.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.assertTrue;


@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@ActiveProfiles("cicd")
class SiteCodeExistsIT {

    @Autowired
    SiteCodeExists siteCodeExists;

    @Test
    void notFoundSiteCodeShouldFail() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSiteNo("xyz");
        stage.setStagedJob(job);
        val codeFound = siteCodeExists.valid(stage);
        Assertions.assertTrue(codeFound.isInvalid());
    }


    @Test
    void existingSiteCodeShouldBeOk() {
        val job = new StagedJob();
        job.setId(1L);
        val stage = new StagedRow();
        stage.setSiteNo("EYR71");
        stage.setStagedJob(job);
        val codeFound = siteCodeExists.valid(stage);
        Assertions.assertTrue(codeFound.isValid());
        val site = codeFound.orElseGet( () -> null);

        Assertions.assertEquals(site.getSiteName(), "South East Slade Point");

    }
}
