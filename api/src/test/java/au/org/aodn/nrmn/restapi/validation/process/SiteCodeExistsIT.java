package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class SiteCodeExistsIT {

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
    void notFoundSiteCodeShouldFail() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        StagedRow row = new StagedRow();
        row.setDate(date);
        row.setDepth("7.1");
        row.setBlock("1");
        row.setMethod("2");
        row.setSiteCode("INVALIDSITECODE");
        row.setStagedJob(job);
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(row));
        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Site Code does not exist")));
    }

    @Test
    void existingSiteCodeShouldBeOk() {
        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        StagedJob job = jobRepo.findByReference("jobid-atrc").get();
        String date = "11/09/2020";
        StagedRow row = new StagedRow();
        row.setDate(date);
        row.setDepth("7.1");
        row.setBlock("1");
        row.setMethod("2");
        row.setSiteCode("ERZ1");
        row.setStagedJob(job);
        Program program = new Program();
        program.setProgramId(1);
        program.setProgramName("RLS");
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(row));
        ValidationResponse response = validationProcess.process(job);
        assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Site Code does not exist")));
    }
}
