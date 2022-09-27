package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.db.model.ObservableItem;
import au.org.aodn.nrmn.db.repository.ObservableItemRepository;
import au.org.aodn.nrmn.db.repository.projections.ObservableItemSuperseded;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservableItemGetDtoMapperConfig {

    @Autowired
    ObservableItemGetDtoMapperConfig(ObservableItemRepository observableItemRepository, ModelMapper modelMapper) {
        modelMapper.typeMap(ObservableItem.class, ObservableItemGetDto.class)
                .setPostConverter(customMappings(observableItemRepository));
    }

    private Converter<ObservableItem, ObservableItemGetDto> customMappings(
            ObservableItemRepository observableItemRepository) {
        return context -> {
            ObservableItem observableItem = context.getSource();
            ObservableItemGetDto observableItemGetDto = context.getDestination();
            ObservableItemSuperseded superseded = observableItemRepository
                    .findSupersededForId(observableItem.getObservableItemId());
            observableItemGetDto.setSupersededIds(superseded.getSupersededIds());
            observableItemGetDto.setSupersededNames(superseded.getSupersededNames());
            return observableItemGetDto;
        };
    }
}
