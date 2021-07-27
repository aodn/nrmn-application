package au.org.aodn.nrmn.restapi.model.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.SurveyMethod.SurveyMethodBuilder;

@Component
public class SurveyMethodTestData {

    @Autowired
    private MethodTestData methodTestData;

    public SurveyMethodBuilder defaultBuilder() {
        final Map<String, String> surveyMethodAttribute = new HashMap<String, String>();
        surveyMethodAttribute.put("NonStandardData", "Site sampled due to oil spill");
        return SurveyMethod.builder()
            .method(methodTestData.persistedMethod())
            .blockNum(1)
            .surveyNotDone(false)
            .surveyMethodAttribute(surveyMethodAttribute);
    }
}
