package au.org.aodn.nrmn.restapi.controller.assembler;

import au.org.aodn.nrmn.restapi.dto.site.SiteListItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;

@Component
public class SiteListItemAssembler {

    private final EntityLinks entityLinks;

    public ModelMapper mapper;

    @Autowired
    public SiteListItemAssembler(ModelMapper mapper, EntityLinks entityLinks) {
        this.mapper = mapper;
        this.entityLinks = entityLinks;
    }
    
    public SiteListItem toModel(Site site) {
        SiteListItem siteListItem =  mapper.map(site, SiteListItem.class);
        return siteListItem.add(entityLinks.linkToItemResource(Site.class, siteListItem.getSiteId()).withSelfRel());
    }
}
