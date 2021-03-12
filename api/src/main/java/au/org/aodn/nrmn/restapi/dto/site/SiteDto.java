package au.org.aodn.nrmn.restapi.dto.site;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SiteDto {
    @Id
    @Schema(title = "Id", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer siteId;

    @NotNull
    @Schema(title = "Site Code")
    private String siteCode;

    @NotNull
    @Schema(title = "Site Name")
    private String siteName;

    @Schema(title = "Longitude")
    private Double longitude;

    @Schema(title = "Latitude")
    private Double latitude;

    @Schema(title = "State")
    private String state;

    @Schema(title = "Country")
    private String country;

    @Type(type = "list-array")
    @ArraySchema(arraySchema = @Schema(title = "Old Site Codes"))
    private List<String> oldSiteCodes;

    @Schema(title = "Marine Protected Area")
    private String mpa;

    @Schema(title = "Protection status")
    private String protectionStatus;

    @Schema(title = "Relief")
    private Integer relief;

    @Schema(title = "Slope")
    private Integer slope;

    @Schema(title = "Wave exposure")
    private Integer waveExposure;

    @Schema(title = "Currents")
    private Integer currents;

    @Schema(title = "Other attributes", accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> siteAttribute;

    @Schema(title = "Is Active", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isActive = false;

    @NotNull
    @Schema(title = "Location")
    private Integer locationId;
}
