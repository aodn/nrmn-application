package au.org.aodn.nrmn.restapi.dto.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

/* Site list page entry */

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteListItem extends RepresentationModel<SiteListItem> {
    @Schema(hidden = true)
    Integer siteId;

    @Schema(title = "Site Code")
    String siteCode;

    @Schema(title = "Site Name")
    String siteName;

    @Schema(title = "Location")
    String locationName;

    @Schema(title = "State")
    String state;

    @Schema(title = "Country")
    String country;

    @Schema(title = "Latitude")
    Double latitude;

    @Schema(title = "Longitude")
    Double longitude;

    @Schema(title = "Is Active")
    Boolean isActive;

    @Schema(hidden = true)
    @Override
    public Links getLinks() {
        return super.getLinks();
    }

}
