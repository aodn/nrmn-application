package au.org.aodn.nrmn.restapi.model.db;

import com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.model.db.SurveyMethod.SurveyMethodBuilder;

@Component
public class SurveyMethodTestData {

    @Autowired
    private MethodTestData methodTestData;

    public SurveyMethodBuilder defaultBuilder() {
        return SurveyMethod.builder()
            .method(methodTestData.persistedMethod())
            .blockNum(1)
            .surveyNotDone(false)
            .surveyMethodAttribute(
                ImmutableMap.<String, String>builder()
                    .put("NonStandardData", "Site sampled due to oil spill")
                    .build());
    }
}
