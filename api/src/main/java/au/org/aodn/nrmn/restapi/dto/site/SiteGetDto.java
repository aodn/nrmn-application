package au.org.aodn.nrmn.restapi.dto.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteGetDto {
    private Integer siteId;
    private String siteCode;
    private String siteName;
    private Float longitude;
    private Float latitude;
    private String state;
    private String country;
    private List<String> oldSiteCodes;
    private String mpa;
    private String protectionStatus;
    private Integer relief;
    private Integer slope;
    private Integer waveExposure;
    private Integer currents;
    private Map<String, String> siteAttribute;
    private Boolean isActive;
    private Integer locationId;
    private String locationName;
}
