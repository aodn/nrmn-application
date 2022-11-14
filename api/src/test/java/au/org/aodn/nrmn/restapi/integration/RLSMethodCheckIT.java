package au.org.aodn.nrmn.restapi.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

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

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class RLSMethodCheckIT {

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
    void methodMissingBlock2ShouldFail() {

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").get();
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

        StagedRow m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        StagedRow m2b2 = (StagedRow) SerializationUtils.clone(m2b1);
        m2b1.setBlock("2");

        StagedRow m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        StagedRow m2b1d8 = (StagedRow) SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m2b1, m2b2, m1b1d8, m2b1d8));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("Survey incomplete")));
    }

    @Test
    void onlyMethod3ShouldFail() {

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").get();
        String date = "11/09/2020";
        String depth = "7";
        String siteNo = "ERZ1";

        StagedRow m0b1 = new StagedRow();
        m0b1.setMethod("0");
        m0b1.setBlock("1");
        m0b1.setDate(date);
        m0b1.setDepth(depth);
        m0b1.setSiteCode(siteNo);
        m0b1.setStagedJob(job);

        StagedRow m0b2 = (StagedRow) SerializationUtils.clone(m0b1);
        m0b2.setBlock("2");

        StagedRow m3b1 = (StagedRow) SerializationUtils.clone(m0b1);
        m3b1.setMethod("3");

        StagedRow m3b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m3b3.setBlock("3");

        StagedRow m5b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m3b3.setBlock("5");
        stagedRowRepo.deleteAll();

        stagedRowRepo.saveAll(Arrays.asList(m0b1, m0b2, m3b1, m3b3, m5b3));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().startsWith("RLS Method must be [0-2], 10 or 12")));
    }

    @Test
    @Transactional
    void onlyDebrisShouldBeMethod12Block2() {

        var location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        var job = jobRepo.findByReference("jobid-rls").get();
        var date = "11/09/2020";
        var depth = "7";
        var siteNo = "ERZ1";

        //RLS Method 12 must be recorded on block 2
        var row1 = new StagedRow();
        row1.setMethod("12");
        row1.setBlock("1");
        row1.setDate(date);
        row1.setDepth(depth);
        row1.setSiteCode(siteNo);
        row1.setStagedJob(job);
        row1.setSpecies("Debris");

        // Method 12 invalid for species
        var row2 = SerializationUtils.clone(row1);
        row1.setMethod("12");
        row2.setBlock("2");
        row2.setSpecies("Species 56");

        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(row1, row2));

        var response = validationProcess.process(job);

        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("RLS Method 12 must be recorded on block 2")));
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Method 12 invalid for species")));
    }

    @Test
    @Transactional
    void debrisMethod12Block2ShouldSucceed() {

        var location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        var job = jobRepo.findByReference("jobid-rls").get();
        var date = "11/09/2020";
        var depth = "7";
        var siteNo = "ERZ1";

        //RLS Method 12 must be recorded on block 2
        var row1 = new StagedRow();
        row1.setMethod("12");
        row1.setBlock("2");
        row1.setDate(date);
        row1.setDepth(depth);
        row1.setSiteCode(siteNo);
        row1.setStagedJob(job);
        row1.setSpecies("Debris");

        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(row1));

        var response = validationProcess.process(job);
        
        assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().contains("Method 12")));
    }

    @Test
    void missingM2ShouldFail() {

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());

        StagedJob job = jobRepo.findByReference("jobid-rls").get();
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

        StagedRow m1b1d10 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d10.setDepth("10");
        StagedRow m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");

        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b1d10, m1b1d8));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream()
                .anyMatch(e -> e.getMessage().contains("missing M2")));
    }
}
