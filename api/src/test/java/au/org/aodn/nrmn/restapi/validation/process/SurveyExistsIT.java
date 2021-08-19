package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.SiteTestData;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedJobTestData;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyTestData;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;

@Testcontainers
@SpringBootTest
@WithNoData
@ExtendWith(PostgresqlContainerExtension.class)
class SurveyExistsIT {

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SurveyTestData surveyTestData;

    @Autowired
    StagedJobTestData stagedJobTestData;

    @Autowired
    SiteTestData siteTestData;
    
    @Autowired
    StagedJobRepository jobRepo;

    @Autowired
    ValidationProcess validationProcess;

    @Test
    void notFoundSurveyShouldSucceed() {
        StagedJob job = stagedJobTestData.persistedJobWithReference("ref");
        StagedRow sn1 = new StagedRow();
        sn1.setDate("11/09/2020");
        sn1.setDepth("7.1");
        sn1.setBlock("1");
        sn1.setMethod("2");
        sn1.setSiteCode("ERZ1");
        sn1.setStagedJob(job);
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(sn1));
        ValidationResponse response = validationProcess.process(job);
        assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:")));
    }

    @Test
    void existingSurveyShouldFail() {
        StagedJob job = stagedJobTestData.persistedJobWithReference("ref");
        Survey survey = surveyTestData.persistedSurvey();
        StagedRow sn1 = new StagedRow();
        sn1.setDate(survey.getSurveyDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sn1.setDepth(survey.getDepth() + "." + survey.getSurveyNum());
        sn1.setMethod("1");
        sn1.setSiteCode(survey.getSite().getSiteCode());
        sn1.setStagedJob(job);
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(sn1));
        ValidationResponse response = validationProcess.process(job);
        assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:")));
    }

    @Test
    void existingSurveyShouldSucceedMethod3() {
        StagedJob job = stagedJobTestData.persistedJobWithReference("ref");
        Survey survey = surveyTestData.persistedSurvey();
        StagedRow sn1 = new StagedRow();
        sn1.setDate(survey.getSurveyDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sn1.setDepth(survey.getDepth() + "." + survey.getSurveyNum());
        sn1.setMethod("3");
        sn1.setSiteCode(survey.getSite().getSiteCode());
        sn1.setStagedJob(job);
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(Arrays.asList(sn1));
        ValidationResponse response = validationProcess.process(job);
        assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:")));
    }
}
