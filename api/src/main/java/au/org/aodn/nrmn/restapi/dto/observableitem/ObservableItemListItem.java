package au.org.aodn.nrmn.restapi.dto.observableitem;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObservableItemListItem extends RepresentationModel<ObservableItemListItem> {
    @Schema(hidden = true)
    private Integer observableItemId;

    @Schema(title = "Name")
    private String observableItemName;

    @Schema(title = "Common name")
    private String commonName;

    @Schema(title = "Superseded by")
    private String supersededBy;

    @Schema(title = "Phylum")
    private String phylum;

    @Schema(title = "Class")
    private String clazz;

    @Schema(title = "Order")
    private String order;

    @Schema(title = "Family")
    private String family;

    @Schema(title = "Genus")
    private String genus;

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }
}
