package au.org.aodn.nrmn.restapi.assembler;

import au.org.aodn.nrmn.restapi.dto.site.SiteListItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;

@Component
public class SiteListItemAssembler {

    private final EntityLinks entityLinks;

    @Autowired
    public SiteListItemAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }
    
    public SiteListItem toModel(Site site) {
        SiteListItem siteListItem =  SiteListItem.builder()
                                                 .siteId(site.getSiteId())
                                                 .siteCode(site.getSiteCode())
                                                 .siteName(site.getSiteName())
                                                 .country(site.getCountry())
                                                 .state(site.getState())
                                                 .isActive(site.getIsActive())
                                                 .latitude(site.getLatitude())
                                                 .longitude(site.getLongitude())
                                                 .locationName(site.getLocation().getLocationName())
                                                 .build();

        return siteListItem.add(entityLinks.linkToItemResource(Site.class, siteListItem.getSiteId()).withSelfRel());
    }
}
