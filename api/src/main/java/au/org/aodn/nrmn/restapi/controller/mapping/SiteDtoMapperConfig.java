package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.db.model.Location;
import au.org.aodn.nrmn.db.model.Site;
import au.org.aodn.nrmn.db.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.dto.site.SiteDto;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SiteDtoMapperConfig {

    @Autowired
    SiteDtoMapperConfig(LocationRepository locationRepository, ModelMapper modelMapper) {
        Converter<Integer, Location> toLocation = ctx -> ctx.getSource() == null ? null
                : locationRepository.findById(ctx.getSource()).orElseThrow(ResourceNotFoundException::new);
        modelMapper.typeMap(SiteDto.class, Site.class).addMappings(mapper -> {
            mapper.using(toLocation).map(SiteDto::getLocationId, Site::setLocation);
            mapper.skip(Site::setSiteId);
        });
    }

}
