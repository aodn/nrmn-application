package au.org.aodn.nrmn.restapi.model.db.projections;

import au.org.aodn.nrmn.restapi.model.db.AphiaRelType;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "selection", types = { AphiaRelType.class })
public interface AphiaRelTypeSelection {

    @JsonProperty("id")
    Integer getAphiaRelTypeId();

    @JsonProperty("label")
    String getAphiaRelTypeName();
}
