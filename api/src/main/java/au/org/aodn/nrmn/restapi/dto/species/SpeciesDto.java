package au.org.aodn.nrmn.restapi.dto.species;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesDto {
    private String phylum;

    @JsonProperty("class")
    private String className;

    private String family;

    private String genus;

    @JsonProperty("species")
    private String scientificName;

    private String order;

}
