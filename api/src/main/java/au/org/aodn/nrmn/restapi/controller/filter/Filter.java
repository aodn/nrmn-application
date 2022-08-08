package au.org.aodn.nrmn.restapi.controller.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class Filter {

    public static final String AND = "and";
    public static final String OR = "or";

    private String fieldName;

    private String operation;

    private Boolean sortBy;

    private String value;

    private List<Filter> conditions;

    public Filter() {}

    public Filter(String fieldName, String value, String operation, Boolean sortBy, List<Filter> conditions) {
        this.fieldName = fieldName;
        this.value = value;
        this.operation = operation;
        this.sortBy = sortBy;
        this.conditions = conditions;
    }

    @JsonIgnore
    public boolean isCompositeCondition() {
        return conditions != null;
    }

    @JsonIgnore
    public boolean isAndOperation() {
        return this.getOperation() != null && this.getOperation().equalsIgnoreCase(AND);
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

    public List<Filter> getConditions() {
        return this.conditions;
    }

    @JsonSetter("field")
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonSetter("ops")
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @JsonSetter("sortBy")
    public void setSortBy(Boolean sortBy) {
        this.sortBy = sortBy;
    }

    @JsonSetter("val")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonSetter("conditions")
    public void setConditions(List<Filter> conditions) {
        this.conditions = conditions;
    }
}
