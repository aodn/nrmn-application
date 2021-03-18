package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeciesDtoMapperConfig {

    @Autowired
    SpeciesDtoMapperConfig(ModelMapper modelMapper) {
        modelMapper.typeMap(ObservableItem.class, SpeciesDto.class)
            .addMapping(ObservableItem::getObservableItemName, SpeciesDto::setScientificName);
    }

}
