package au.org.aodn.nrmn.restapi.model.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity;
import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity.SurveyMethodEntityBuilder;
import au.org.aodn.nrmn.restapi.data.repository.SurveyMethodRepository;

@Component
public class SurveyMethodTestData {

    @Autowired
    private MethodTestData methodTestData;

    @Autowired
    private SurveyMethodRepository methodRepository;

    public SurveyMethodEntity persistedSurveyMethod(SurveyMethodEntity entity) {
        methodRepository.saveAndFlush(entity);
        return entity;
    }

    public SurveyMethodEntity buildWith(Survey survey, int itemNumber) {
        final Map<String, String> surveyMethodAttribute = new HashMap<String, String>();
        surveyMethodAttribute.put("Item number", String.valueOf(itemNumber));
        return SurveyMethodEntity.builder()
                .method(methodTestData.persistedMethod())
                .survey(survey)
                .blockNum(1)
                .surveyNotDone(false)
                .surveyMethodAttribute(surveyMethodAttribute)
                .build();
    }

    public SurveyMethodEntityBuilder defaultBuilder() {
        final Map<String, String> surveyMethodAttribute = new HashMap<String, String>();
        surveyMethodAttribute.put("NonStandardData", "Site sampled due to oil spill");
        return SurveyMethodEntity.builder()
            .method(methodTestData.persistedMethod())
            .blockNum(1)
            .surveyNotDone(false)
            .surveyMethodAttribute(surveyMethodAttribute);
    }
}
