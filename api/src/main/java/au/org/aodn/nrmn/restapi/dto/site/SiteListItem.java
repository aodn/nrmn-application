package au.org.aodn.nrmn.restapi.dto.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Site list page entry */

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteListItem {
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
    Float latitude;

    @Schema(title = "Longitude")
    Float longitude;

    @Schema(title = "Is Active")
    Boolean isActive;
}
