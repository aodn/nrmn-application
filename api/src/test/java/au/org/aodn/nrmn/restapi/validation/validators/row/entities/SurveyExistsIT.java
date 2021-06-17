package au.org.aodn.nrmn.restapi.validation.validators.row.entities;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.row.entities.SurveyExists;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@WithNoData
@ExtendWith(PostgresqlContainerExtension.class)
class SurveyExistsIT {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SurveyTestData surveyTestData;

    @Autowired
    StagedJobTestData stagedJobTestData;

    @Autowired
    SiteTestData siteTestData;

    @Test
    void notFoundSurveyShouldSucceed() {
        val stagedJob = stagedJobTestData.persistedJobWithReference("ref");
        StagedRowFormatted formattedRow = StagedRowFormatted
                .builder()
                .site(siteTestData.persistedSite())
                .depth(6)
                .surveyNum(1)
                .method(1)
                .date(LocalDate.parse("2020-05-21"))
                .ref(stagedJob.getRows().get(0))
                .build();
        val duplicateSurveyValidation = new SurveyExists(surveyRepository);
        val result = duplicateSurveyValidation.valid(formattedRow);
        assertTrue(result.isValid());
    }

    @Test
    void existingSurveyShouldFail() {
        val stagedJob = stagedJobTestData.persistedJobWithReference("ref");
        Survey survey = surveyTestData.persistedSurvey();
        StagedRowFormatted formattedRow = StagedRowFormatted
                .builder()
                .site(survey.getSite())
                .depth(survey.getDepth())
                .surveyNum(survey.getSurveyNum())
                .date(survey.getSurveyDate().toLocalDate())
                .method(1)
                .ref(stagedJob.getRows().get(0))
                .build();
        val duplicateSurveyValidation = new SurveyExists(surveyRepository);
        val result = duplicateSurveyValidation.valid(formattedRow);
        assertTrue(result.isInvalid());
    }

    @Test
    void existingSurveyShouldSucceedMethod3() {
        val stagedJob = stagedJobTestData.persistedJobWithReference("ref");
        Survey survey = surveyTestData.persistedSurvey();
        StagedRowFormatted formattedRow = StagedRowFormatted
                .builder()
                .site(survey.getSite())
                .depth(survey.getDepth())
                .surveyNum(survey.getSurveyNum())
                .date(survey.getSurveyDate().toLocalDate())
                .method(3)
                .ref(stagedJob.getRows().get(0))
                .build();
        val duplicateSurveyValidation = new SurveyExists(surveyRepository);
        val result = duplicateSurveyValidation.valid(formattedRow);
        assertTrue(result.isValid());
    }
}
