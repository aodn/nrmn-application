package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemSupersededBy;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservableItemGetDtoMapperConfig {

    @Autowired
    ObservableItemGetDtoMapperConfig(ObservableItemRepository observableItemRepository, ModelMapper modelMapper) { 
        modelMapper.typeMap(ObservableItem.class, ObservableItemGetDto.class).setPostConverter(customMappings(observableItemRepository));
    }
    private Converter<ObservableItem, ObservableItemGetDto> customMappings(ObservableItemRepository observableItemRepository) {
        return context -> {
            ObservableItem observableItem = context.getSource();
            ObservableItemGetDto observableItemGetDto = context.getDestination();
            ObservableItemSupersededBy supersededBy = observableItemRepository.findSupersededForId(observableItem.getObservableItemId());
            observableItemGetDto.setSupersededByIds(supersededBy.getSupersededByIds());
            observableItemGetDto.setSupersededByNames(supersededBy.getSupersededByNames());
            return observableItemGetDto;
        };
    }
}
