package au.org.aodn.nrmn.restapi.model.db.projections;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "selection", types = { AphiaRef.class })
public interface AphiaRefSelection {

    @JsonProperty("id")
    Integer getAphiaId();

    @JsonProperty("label")
    String getScientificName();
}
