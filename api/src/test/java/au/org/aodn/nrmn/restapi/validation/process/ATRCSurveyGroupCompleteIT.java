package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.SerializationUtils;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class ATRCSurveyGroupCompleteIT {

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    ValidationProcess validationProcess;

    @Test
    void groupWithAllSurveyNumsShouldSucceed() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        String siteNo = "ERZ1";
        StagedRow sn1 = new StagedRow();
        sn1.setDate(date);
        sn1.setDepth("7.1");
        sn1.setBlock("1");
        sn1.setMethod("2");
        sn1.setSiteCode(siteNo);
        sn1.setStagedJob(job);
        StagedRow sn2 = (StagedRow)SerializationUtils.clone(sn1);
        sn2.setDepth("7.2");
        StagedRow sn3 = (StagedRow)SerializationUtils.clone(sn1);
        sn3.setDepth("7.3");
        StagedRow sn4 = (StagedRow)SerializationUtils.clone(sn1);
        sn4.setDepth("7.4");
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2, sn3, sn4));
        ValidationResponse response = validationProcess.process(job);
        assertTrue(!response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey group incomplete")));
    }

    @Test
    void groupWithIncompleteSurveyNumsShouldFail() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        String siteNo = "ERZ1";

        StagedRow sn1 = new StagedRow();
        sn1.setDate(date);
        sn1.setDepth("7.1");
        sn1.setBlock("1");
        sn1.setMethod("2");
        sn1.setSiteCode(siteNo);
        sn1.setStagedJob(job);

        StagedRow sn2 = (StagedRow) SerializationUtils.clone(sn1);
        sn2.setDepth("7.2");
        stagedRowRepo.deleteAll();

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey group incomplete")));
    }

    @Test
    void groupWithCompleteSurveyBlocksShouldSucceed() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        String siteNo = "ERZ1";

        StagedRow sn1b1 = new StagedRow();
        sn1b1.setDate(date);
        sn1b1.setDepth("7.1");
        sn1b1.setMethod("1");
        sn1b1.setBlock("1");
        sn1b1.setSiteCode(siteNo);
        sn1b1.setStagedJob(job);

        StagedRow sn1b2 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn1b2.setBlock("2");

        StagedRow sn2b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn2b1.setDepth("7.2");
        StagedRow sn2b2 = (StagedRow) SerializationUtils.clone(sn2b1);
        sn2b2.setBlock("2");

        StagedRow sn3b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn3b1.setDepth("7.3");
        StagedRow sn3b2 = (StagedRow) SerializationUtils.clone(sn3b1);
        sn3b2.setBlock("2");

        StagedRow sn4b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn4b1.setDepth("7.4");
        StagedRow sn4b2 = (StagedRow) SerializationUtils.clone(sn4b1);
        sn4b2.setBlock("2");

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        stagedRowRepo.saveAll(Arrays.asList(sn1b1, sn1b2, sn2b1, sn2b2, sn3b1, sn3b2, sn4b1, sn4b2));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(!response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey group incomplete")));
    }

    @Test
    void groupWithIncompleteSurveyBlocksShouldFail() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        String siteNo = "ERZ1";

        StagedRow sn1b1 = new StagedRow();
        sn1b1.setDate(date);
        sn1b1.setDepth("7.1");
        sn1b1.setMethod("1");
        sn1b1.setBlock("1");
        sn1b1.setSiteCode(siteNo);
        sn1b1.setStagedJob(job);

        StagedRow sn1b2 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn1b2.setBlock("2");

        StagedRow sn2b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn2b1.setDepth("7.2");
        StagedRow sn2b2 = (StagedRow) SerializationUtils.clone(sn2b1);
        sn2b2.setBlock("2");

        StagedRow sn3b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn3b1.setDepth("7.3");
        StagedRow sn3b2 = (StagedRow) SerializationUtils.clone(sn3b1);
        sn3b2.setBlock("2");

        // Incomplete - missing block 2
        StagedRow sn4b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn4b1.setDepth("7.4");

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        stagedRowRepo.saveAll(Arrays.asList(sn1b1, sn1b2, sn2b1, sn2b2, sn3b1, sn3b2, sn4b1));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7.1 survey incomplete: missing M2, M3")));
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7.2 survey incomplete: missing M2, M3")));
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7.3 survey incomplete: missing M2, M3")));
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7.4 survey incomplete: missing M2, M3")));
    }

}
