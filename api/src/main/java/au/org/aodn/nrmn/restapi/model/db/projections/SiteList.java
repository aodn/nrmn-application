package au.org.aodn.nrmn.restapi.model.db.projections;

import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import org.springframework.data.rest.core.config.Projection;

import java.util.Map;

/* Add projection that includes location details when listing sites */

@Projection(name = "list", types = {Site.class})
public interface SiteList {

    Integer getSiteId();

    String getSiteCode();

    String getSiteName();

    Double getLongitude();

    Double getLatitude();

    Map<String, Object> getSiteAttribute();

    Boolean getIsActive();

    Location getLocation();
}
