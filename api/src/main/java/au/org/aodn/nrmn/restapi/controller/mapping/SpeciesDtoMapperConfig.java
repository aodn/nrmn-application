package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.controller.exception.SiteLocationNotFoundException;
import au.org.aodn.nrmn.restapi.dto.site.SiteDto;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeciesDtoMapperConfig {

    @Autowired
    SpeciesDtoMapperConfig(ModelMapper modelMapper) {
        modelMapper.typeMap(ObservableItem.class, SpeciesDto.class)
            .addMapping(ObservableItem::getPhylum, SpeciesDto::setRankPhylum)
            .addMapping(ObservableItem::getClazz, SpeciesDto::setRankClass)
            .addMapping(ObservableItem::getFamily, SpeciesDto::setRankFamily)
            .addMapping(ObservableItem::getGenus, SpeciesDto::setRankGenus)
            .addMapping(ObservableItem::getObservableItemName, SpeciesDto::setRankSpecies)
            .addMapping(ObservableItem::getOrder, SpeciesDto::setRankOrder);
    }

}
