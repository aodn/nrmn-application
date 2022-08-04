package au.org.aodn.nrmn.restapi.controller.filter;

import au.org.aodn.nrmn.restapi.repository.dynamicQuery.SurveyFilterCondition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.Optional;

public class Filter {

    @JsonSetter("field")
    private String fieldName;

    @JsonSetter("ops")
    private String operation;

    @JsonSetter("sortBy")
    private Boolean sortBy;

    @JsonSetter("val")
    private String value;

    @JsonSetter("conditions")
    private List<SurveyFilterCondition> conditions;

    @JsonIgnore
    public boolean isCompositeCondition() {
        return conditions != null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperation() {
        return operation;
    }

    public String getValue() {
        return value;
    }
}
