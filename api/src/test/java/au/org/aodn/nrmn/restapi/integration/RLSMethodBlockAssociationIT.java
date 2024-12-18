package au.org.aodn.nrmn.restapi.integration;

import au.org.aodn.nrmn.restapi.data.model.Location;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class RLSMethodBlockAssociationIT {

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    ValidationProcess validationProcess;

    @Autowired
    LocationRepository locationRepository;

    @Test
    void expectedAssociationShouldSucceed() {
        stagedRowRepo.deleteAll();

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);

        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").orElse(null);
        Assertions.assertNotNull(job);

        String date = "11/09/2020";
        String depth = "7";
        String siteNo = "ERZ1";

        StagedRow m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        StagedRow m1b2 = SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        StagedRow m2b1 = SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");

        StagedRow m2b2 = SerializationUtils.clone(m2b1);
        m2b2.setBlock("2");

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1, m2b2));

        ValidationResponse response = validationProcess.process(job);
        assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7")));
    }

    @Test
    void missingOnBlockShouldFail() {
        stagedRowRepo.deleteAll();

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);

        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").orElse(null);
        Assertions.assertNotNull(job);

        String date = "11/09/2020";
        String depth = "7.1";
        String siteNo = "ERZ1";

        StagedRow m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        StagedRow m1b2 = SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        StagedRow m2b1 = SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        // Missing Block 2

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().startsWith("Survey incomplete: [ERZ1, 2020-09-11, 7.1] M2 missing B2")));
    }

    @Test
    void missingM0BlockShouldFail() {
        stagedRowRepo.deleteAll();

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);

        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").orElse(null);
        Assertions.assertNotNull(job);

        String date = "11/09/2020";
        String depth = "7.1";
        String siteNo = "ERZ1";

        StagedRow m1b1 = new StagedRow();
        m1b1.setMethod("0");
        m1b1.setBlock("3");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        StagedRow m1b2 = SerializationUtils.clone(m1b1);
        m1b2.setBlock("4");

        StagedRow m2b1 = SerializationUtils.clone(m1b1);
        m2b1.setMethod("5");
        // M0 missing B0 or B1 or 2

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().startsWith("Method 0 must have block 0, 1 or 2")));
    }

    @Test
    void missingM10BlockShouldFail() {
        stagedRowRepo.deleteAll();

        var location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);

        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        var job = jobRepo.findByReference("jobid-rls").orElse(null);
        Assertions.assertNotNull(job);

        var date = "11/09/2020";
        var depth = "7.1";
        var siteNo = "ERZ1";

        var m1b1 = new StagedRow();
        m1b1.setMethod("10");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        var m1b2 = SerializationUtils.clone(m1b1);
        m1b2.setBlock("1");

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2));

        var response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().startsWith("M10 requires B1 and B2")));
    }

    @Test
    void completeM10BlockShouldSucceed() {
        stagedRowRepo.deleteAll();

        var location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);

        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        var job = jobRepo.findByReference("jobid-rls").orElse(null);
        Assertions.assertNotNull(job);

        var date = "11/09/2020";
        var depth = "7.1";
        var siteNo = "ERZ1";

        var row1 = new StagedRow();
        row1.setMethod("10");
        row1.setBlock("1");
        row1.setDate(date);
        row1.setDepth(depth);
        row1.setSiteCode(siteNo);
        row1.setStagedJob(job);
        var row2 = SerializationUtils.clone(row1);
        var row3 = SerializationUtils.clone(row1);
        var row4 = SerializationUtils.clone(row1);

        row2.setBlock("1");
        row3.setBlock("2");
        row4.setBlock("2");

        stagedRowRepo.saveAll(Arrays.asList(row1, row2, row3, row4));

        var response = validationProcess.process(job);
        assertFalse(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().startsWith("M10 requires B1 and B2")));
    }
}
