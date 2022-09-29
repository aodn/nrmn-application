package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.model.Survey.SurveyBuilder;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SurveyTestData {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private ProgramTestData programTestData;

    private LocalDate date = LocalDate.of(2022, 11, 01);

    public Survey persistedSurvey() {
        Survey survey = defaultBuilder().build();
        surveyRepository.saveAndFlush(survey);
        return survey;
    }

    public void persistedSurvey(List<Survey> surveyList) {
        surveyList.forEach(a -> {
            programTestData.persistedProgram(a.getProgram());
            siteTestData.persistedSite(a.getSite());
            surveyRepository.saveAndFlush(a);
        });
    }

    public Survey buildWith(int itemNumber) {
        return Survey.builder()
                .program(programTestData.buildWith(itemNumber))
                .site(siteTestData.buildWith(itemNumber))
                .surveyDate(Date.valueOf(date.plusDays(itemNumber)))
                .surveyTime(Time.valueOf("23:37:00"))
                .depth(itemNumber)
                .surveyNum(itemNumber + 100)
                .visibility(null)
                .direction(null)
                .blockAbundanceSimulated(true)
                .created(Timestamp.valueOf(LocalDateTime.now()))
                .updated(Timestamp.valueOf(LocalDateTime.now()))
                .build();
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
                     .blockAbundanceSimulated(true)
                     .created(Timestamp.valueOf(LocalDateTime.now()))
                     .updated(Timestamp.valueOf(LocalDateTime.now()));
    }

}
