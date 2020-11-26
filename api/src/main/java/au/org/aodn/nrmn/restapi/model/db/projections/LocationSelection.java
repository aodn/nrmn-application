package au.org.aodn.nrmn.restapi.model.db.projections;

import au.org.aodn.nrmn.restapi.model.db.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "selection", types = { Location.class })
public interface LocationSelection {
    
    @JsonProperty("id")
    Integer getLocationId();
    
    @JsonProperty("label")
    String getLocationName();
}
