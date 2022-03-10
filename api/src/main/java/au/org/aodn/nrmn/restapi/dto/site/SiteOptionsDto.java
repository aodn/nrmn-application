package au.org.aodn.nrmn.restapi.dto.site;

import java.util.List;

import au.org.aodn.nrmn.restapi.model.db.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SiteOptionsDto {
    private List<Location> locations;
    private List<String> marineProtectedAreas;
    private List<String> protectionStatuses;
    private List<String> siteStates;
    private List<String> siteCountries;
}
