package au.org.aodn.nrmn.restapi.data.repository.projections;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.org.aodn.nrmn.restapi.data.model.Location;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "selection", types = { Location.class })
public interface LocationSelection {
    
    @JsonProperty("id")
    Integer getLocationId();
    
    @JsonProperty("label")
    String getLocationName();
}
