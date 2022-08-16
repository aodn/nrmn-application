package au.org.aodn.nrmn.restapi.controller.transform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Sorter implements Field {

    public static final String ASC = "asc";

    private String fieldName;
    private String order;

    @JsonIgnore
    public boolean isAsc() {
        return ASC.equalsIgnoreCase(order);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @JsonSetter("field")
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOrder() {
        return order;
    }

    @JsonSetter("order")
    public void setOrder(String order) {
        this.order = order;
    }
}
