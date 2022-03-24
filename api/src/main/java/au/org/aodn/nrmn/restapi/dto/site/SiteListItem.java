package au.org.aodn.nrmn.restapi.dto.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/* Site list page entry */

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteListItem {
    Integer siteId;
    String siteCode;
    String siteName;
    String locationName;
    String state;
    String country;
    Float latitude;
    Float longitude;
    Boolean isActive;
}
