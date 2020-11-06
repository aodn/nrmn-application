package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.SurveyMethod.SurveyMethodBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurveyMethodTestData {

    @Autowired
    private ObservationTestData observationTestData;

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
