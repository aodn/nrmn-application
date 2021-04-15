package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpeciesDtoMapperConfig {

    @Autowired
    SpeciesDtoMapperConfig(ModelMapper modelMapper) {
        modelMapper.typeMap(ObservableItem.class, SpeciesDto.class)
                   .addMapping(ObservableItem::getObservableItemName, SpeciesDto::setScientificName);

        modelMapper.typeMap(SpeciesRecord.class, SpeciesDto.class)
                   .setPostConverter(customMappings());
    }

    private Converter<SpeciesRecord, SpeciesDto> customMappings() {
        return context -> {
            SpeciesRecord speciesRecord = context.getSource();
            SpeciesDto speciesDto = context.getDestination();

            if (speciesRecord.getStatus().equalsIgnoreCase("unaccepted")) {
                speciesDto.setSupersededBy(speciesRecord.getValidName());
                speciesDto.setSupersededById(speciesRecord.getValidAphiaId());
            }

            return speciesDto;
        };
    }

}
