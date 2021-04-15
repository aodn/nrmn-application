package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservableItemDtoMapperConfig {

    @Autowired
    ObservableItemDtoMapperConfig(ObsItemTypeRepository obsItemTypeRepository, ModelMapper modelMapper) { 
        Converter<Integer, ObsItemType> toObsItemType = ctx -> ctx.getSource() == null ? null :
        obsItemTypeRepository.findById(ctx.getSource()).orElseThrow(ResourceNotFoundException::new);
        modelMapper.typeMap(ObservableItemDto.class, ObservableItem.class).addMappings(mapper -> {
            mapper.using(toObsItemType).map(ObservableItemDto::getObsItemTypeId, ObservableItem::setObsItemType);
            mapper.skip(ObservableItem::setObservableItemId);
        });
    }

}
