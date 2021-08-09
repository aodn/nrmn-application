package au.org.aodn.nrmn.restapi.dto.site;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SiteDto {
    @Id
    @Schema(title = "Id", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer siteId;

    @NotEmpty(message = "Site Code is required")
    @Schema(title = "Site Code")
    private String siteCode;

    @NotEmpty(message = "Site Name is required")
    @Schema(title = "Site Name")
    private String siteName;

    @NotNull(message = "Longitude is required")
    @Schema(title = "Longitude")
    private Float longitude;

    @NotNull(message = "Latitude is required")
    @Schema(title = "Latitude")
    private Float latitude;

    @NotEmpty(message = "State is required")
    @Schema(title = "State")
    private String state;

    @NotEmpty(message = "Country is required")
    @Schema(title = "Country")
    private String country;

    @Type(type = "list-array")
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

    @NotNull(message = "Location is required")
    @Schema(title = "Location")
    private Integer locationId;
}
