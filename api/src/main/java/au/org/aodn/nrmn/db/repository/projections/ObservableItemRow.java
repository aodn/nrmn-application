package au.org.aodn.nrmn.db.repository.projections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ObservableItemRow {

    Integer getObservableItemId();

    String getLetterCode();

    String getTypeName();

    String getName();

    String getCommonName();

    String getSupersededBy();

    String getSupersededNames();

    String getSupersededIDs();

    String getPhylum();

    @JsonProperty(value = "class")
    String getClassName();

    String getOrder();

    String getFamily();

    String getGenus();
}
