package au.org.aodn.nrmn.restapi.model.db;

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
    private SiteTestData siteTestData;

    @Autowired
    private ProgramTestData programTestData;
    
    @Autowired
    private MethodTestData methodTestData;
    
    @Autowired
    private DiverTestData diverTestData;
    
    @Autowired
    private ObservableItemTestData observableItemTestData;
    
    @Autowired
    private MeasureTestData measureTestData;

    public Survey persistedSurvey() {

        val observation = Observation.builder()
            .diver(diverTestData.persistedDiver())
            .observableItem(observableItemTestData.persistedObservableItem())
            .measure(measureTestData.persistedMeasure())
            .measureValue(4)
            .observationAttribute(
                ImmutableMap.<String, String>builder().put("Biomass", "0.7630353218")
                    .build()
            )
            .build();
            
        val observations = ImmutableSet.<Observation>builder().add(observation).build();
            
        val surveyMethodAttributes = ImmutableMap.<String, String>builder()
            .put("NonStandardData", "Site sampled due to oil spill")
            .build();
            
        val surveyMethod = SurveyMethod.builder()
            .method(methodTestData.persistedMethod())
            .blockNum(1)
            .surveyNotDone(false)
            .surveyMethodAttribute(surveyMethodAttributes)
            .build();

        surveyMethod.setObservations(observations);
        
        val surveyMethods = ImmutableSet.<SurveyMethod>builder().add(surveyMethod).build();

        val surveyAttributes = ImmutableMap.<String, String>builder()
            .put("BlockAbundanceSimulated", "True")
            .build();

        val survey = Survey.builder()
            .site(siteTestData.persistedSite())
            .program(programTestData.persistedProgram())
            .surveyDate(Date.valueOf("2004-11-21"))
            .surveyTime(Time.valueOf("23:37:00"))
            .depth(2)
            .surveyNum(1)
            .visibility(null)
            .direction(null)
            .surveyAttribute(surveyAttributes)
            .build();
            
        survey.setSurveyMethods(surveyMethods);
        
        surveyRepository.saveAndFlush(survey);
        return survey;
    }
}
