package au.org.aodn.nrmn.restapi.dto.species;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeciesDto {
    @JsonProperty("phylum")
    private String rankPhylum;

    @JsonProperty("class")
    private String rankClass;

    @JsonProperty("family")
    private String rankFamily;

    @JsonProperty("genus")
    private String rankGenus;

    @JsonProperty("species")
    private String rankSpecies;

    @JsonProperty("order")
    private String rankOrder;

}
