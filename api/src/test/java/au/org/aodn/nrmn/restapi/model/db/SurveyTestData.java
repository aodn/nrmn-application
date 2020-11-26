package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Survey.SurveyBuilder;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;

@Component
public class SurveyTestData {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyMethodTestData surveyMethodTestData;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private ProgramTestData programTestData;

    public Survey persistedSurvey() {
        val survey = defaultBuilder().build();
        surveyRepository.saveAndFlush(survey);
        return survey;
    }

    public SurveyBuilder defaultBuilder() {
        return Survey.builder()
            .program(programTestData.persistedProgram())
            .site(siteTestData.persistedSite())
            .surveyDate(Date.valueOf("2004-11-21"))
            .surveyTime(Time.valueOf("23:37:00"))
            .depth(2)
            .surveyNum(1)
            .visibility(null)
            .direction(null)
            .surveyAttribute(
                ImmutableMap.<String, String>builder()
                    .put("BlockAbundanceSimulated", "True")
                    .build());
    }

}
