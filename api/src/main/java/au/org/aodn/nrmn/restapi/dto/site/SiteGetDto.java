package au.org.aodn.nrmn.restapi.dto.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteGetDto {
    @Schema(title = "Id")
    private Integer siteId;

    @Schema(title = "Site Code")
    private String siteCode;

    @Schema(title = "Site Name")
    private String siteName;

    @Schema(title = "Longitude")
    private Float longitude;

    @Schema(title = "Latitude")
    private Float latitude;

    @Schema(title = "State")
    private String state;

    @Schema(title = "Country")
    private String country;

    @ArraySchema(arraySchema = @Schema(title = "Old Site Codes"))
    private List<String> oldSiteCodes;

    @Schema(title = "Marine Protected Area")
    private String mpa;

    @Schema(title = "Protection Status")
    private String protectionStatus;

    @Schema(title = "Relief")
    private Integer relief;

    @Schema(title = "Slope")
    private Integer slope;

    @Schema(title = "Wave Exposure")
    private Integer waveExposure;

    @Schema(title = "Currents")
    private Integer currents;

    @Schema(title = "Other Attributes", accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> siteAttribute;

    @Schema(title = "Is Active")
    private Boolean isActive;

    @Schema(title = "Location")
    private Integer locationId;

    @Schema(title = "Location")
    private String locationName;
}
