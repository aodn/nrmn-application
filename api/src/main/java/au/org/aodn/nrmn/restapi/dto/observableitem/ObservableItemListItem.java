package au.org.aodn.nrmn.restapi.dto.observableitem;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservableItemListItem extends RepresentationModel<ObservableItemListItem> {
    @Schema(title = "ID")
    private Integer id;

    @Schema(title = "Type")
    private String obsItemTypeName;

    @Schema(title = "Name")
    private String name;

    @Schema(title = "Common name")
    private String commonName;

    @Schema(title = "Superseded By")
    private String supersededBy;

    @Schema(title = "Superseded Names")
    private String supersededNames;

    @Schema(title = "Superseded IDs")
    private String supersededIDs;

    @Schema(title = "Phylum")
    private String phylum;

    @Schema(title = "Class")
    @JsonProperty("class")
    private String clazz;

    @Schema(title = "Order")
    private String order;

    @Schema(title = "Family")
    private String family;

    @Schema(title = "Genus")
    private String genus;
}
