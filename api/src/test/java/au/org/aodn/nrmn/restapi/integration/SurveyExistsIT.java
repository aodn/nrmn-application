package au.org.aodn.nrmn.restapi.integration;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.model.db.StagedJobTestData;
import au.org.aodn.nrmn.restapi.model.db.SurveyTestData;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@WithNoData
@Transactional
@ExtendWith(PostgresqlContainerExtension.class)
class SurveyExistsIT {

    @Autowired
    StagedRowRepository stagedRowRepo;

    @Autowired
    SurveyTestData surveyTestData;

    @Autowired
    StagedJobTestData stagedJobTestData;

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
        stagedRowRepo.saveAll(List.of(sn1));
        ValidationResponse response = validationProcess.process(job);
        Assertions.assertFalse(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:")));
        Assertions.assertEquals(0, response.getExistingSurveyCount());
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
        stagedRowRepo.saveAll(List.of(sn1));
        ValidationResponse response = validationProcess.process(job);
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:") && e.getLevelId() == ValidationLevel.BLOCKING));
        Assertions.assertEquals(1, response.getExistingSurveyCount());
    }

    @Test
    void existingSurveyShouldWarnMethod3() {
        StagedJob job = stagedJobTestData.persistedJobWithReference("ref");
        Survey survey = surveyTestData.persistedSurvey();
        StagedRow sn1 = new StagedRow();
        sn1.setDate(survey.getSurveyDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sn1.setDepth(survey.getDepth() + "." + survey.getSurveyNum());
        sn1.setMethod("3");
        sn1.setSiteCode(survey.getSite().getSiteCode());
        sn1.setStagedJob(job);
        stagedRowRepo.deleteAll();
        stagedRowRepo.saveAll(List.of(sn1));
        ValidationResponse response = validationProcess.process(job);
        Assertions.assertTrue(response.getErrors().stream().anyMatch(e -> e.getMessage().startsWith("Survey exists:") && e.getLevelId() == ValidationLevel.WARNING));
        Assertions.assertEquals(1, response.getExistingSurveyCount());
    }
}
