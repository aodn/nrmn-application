package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
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
        
        val job = jobRepo.findByReference("jobid-rls").get();
        val date = "11/09/2020";
        val depth = "7";
        val siteNo = "ERZ1";

        val m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        val m1b2 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        val m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");

        val m2b2 = (StagedRow) SerializationUtils.clone(m2b1);
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

        val job = jobRepo.findByReference("jobid-rls").get();
        val date = "11/09/2020";
        val depth = "7";
        val siteNo = "ERZ1";

        val m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setStagedJob(job);

        val m1b2 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b2.setBlock("2");

        val m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        // Missing Block 2

        stagedRowRepo.saveAll(Arrays.asList(m1b1, m1b2, m2b1));

        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("ERZ1/11/09/2020/7 survey incomplete: M2 missing B2")));
    }
}
