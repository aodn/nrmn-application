package au.org.aodn.nrmn.restapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.Location;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@Transactional
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
        StagedJob job = jobRepo.findByReference("jobid-atrc").orElse(null);
        Assertions.assertNotNull(job);

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
        StagedRow sn3 = (StagedRow) SerializationUtils.clone(sn1);
        sn3.setDepth("7.3");
        StagedRow sn4 = (StagedRow) SerializationUtils.clone(sn1);
        sn4.setDepth("7.4");
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2, sn3, sn4));
        ValidationResponse response = validationProcess.process(job);
        Assertions.assertTrue(response.getErrors().stream().noneMatch(e -> e.getMessage().contains("missing transect")));
    }

    @Test
    void groupWithIncompleteSurveyNumsShouldFail() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").orElse(null);
        Assertions.assertNotNull(job);

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

        StagedRow sn4 = (StagedRow) SerializationUtils.clone(sn1);
        sn4.setDepth("7.4");
        stagedRowRepo.deleteAll();

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        stagedRowRepo.saveAll(Arrays.asList(sn1, sn2, sn4));

        ValidationResponse response = validationProcess.process(job);
        Optional<SurveyValidationError> surveyGroupValidation = response.getErrors().stream().filter(e -> e.getMessage().startsWith("Survey group [ERZ1, 2020-09-11, 7] missing transect 3")).findFirst();
        Assertions.assertTrue(surveyGroupValidation.isPresent());
        assertEquals(ValidationLevel.WARNING, surveyGroupValidation.get().getLevelId());
    }

    @Test
    void groupWithCompleteSurveyBlocksShouldSucceed() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").orElse(null);
        Assertions.assertNotNull(job);

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
        Assertions.assertTrue(response.getErrors().stream().noneMatch(e -> e.getMessage().contains("missing transect")));
    }

    @Test
    void groupWithDifferentDateFormatShouldSucceed() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").orElse(null);
        Assertions.assertNotNull(job);

        String date1 = "11/09/2020";
        String date2 = "11/09/20";

        String siteNo = "ERZ1";

        StagedRow sn1b1 = new StagedRow();
        sn1b1.setDate(date1);
        sn1b1.setDepth("7.1");
        sn1b1.setMethod("1");
        sn1b1.setBlock("1");
        sn1b1.setSiteCode(siteNo);
        sn1b1.setStagedJob(job);

        StagedRow sn1b2 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn1b2.setDate(date1);
        sn1b2.setBlock("2");

        StagedRow sn2b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn2b1.setDate(date1);
        sn2b1.setDepth("7.2");
        StagedRow sn2b2 = (StagedRow) SerializationUtils.clone(sn2b1);
        sn2b2.setDate(date2);
        sn2b2.setBlock("2");

        StagedRow sn3b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn3b1.setDate(date2);
        sn3b1.setDepth("7.3");
        StagedRow sn3b2 = (StagedRow) SerializationUtils.clone(sn3b1);
        sn3b2.setDate(date2);
        sn3b2.setBlock("2");

        StagedRow sn4b1 = (StagedRow) SerializationUtils.clone(sn1b1);
        sn4b1.setDate(date2);
        sn4b1.setDepth("7.4");
        StagedRow sn4b2 = (StagedRow) SerializationUtils.clone(sn4b1);
        sn4b2.setBlock("2");

        Location location = Location.builder().locationName("LOC1").isActive(false).build();
        locationRepository.save(location);
        siteRepository.save(Site.builder().siteName("ERZ1").siteCode("ERZ1").location(location).isActive(true).build());
        stagedRowRepo.saveAll(Arrays.asList(sn1b1, sn1b2, sn2b1, sn2b2, sn3b1, sn3b2, sn4b1, sn4b2));

        ValidationResponse response = validationProcess.process(job);
        Assertions.assertTrue(response.getErrors().stream().noneMatch(e -> e.getMessage().contains("missing transect")));
    }


    @Test
    void groupWithIncompleteSurveyBlocksShouldFail() {
        StagedJob job = jobRepo.findByReference("jobid-atrc").orElse(null);
        Assertions.assertNotNull(job);

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
        var errors = response.getErrors()
                .stream()
                .map(SurveyValidationError::getMessage)
                .filter(m -> m.contains("incomplete"))
                .sorted()
                .toArray();

        Assertions.assertEquals(4, errors.length);
        Assertions.assertEquals("Survey incomplete: [ERZ1, 2020-09-11, 7.1] missing M2, M3", errors[0]);
        Assertions.assertEquals("Survey incomplete: [ERZ1, 2020-09-11, 7.2] missing M2, M3", errors[1]);
        Assertions.assertEquals("Survey incomplete: [ERZ1, 2020-09-11, 7.3] missing M2, M3", errors[2]);
        Assertions.assertEquals("Survey incomplete: [ERZ1, 2020-09-11, 7.4] missing M2, M3. M1 missing B2", errors[3]);
    }

}
