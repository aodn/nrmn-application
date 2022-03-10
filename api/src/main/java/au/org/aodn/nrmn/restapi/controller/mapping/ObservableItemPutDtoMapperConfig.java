
package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservableItemPutDtoMapperConfig {

    @Autowired
    ObservableItemPutDtoMapperConfig(ObsItemTypeRepository obsItemTypeRepository, ModelMapper modelMapper) {
        Converter<Integer, ObsItemType> toObsItemType = ctx -> ctx.getSource() == null ? null
                : obsItemTypeRepository.findById(ctx.getSource()).orElseThrow(ResourceNotFoundException::new);
        modelMapper.typeMap(ObservableItemPutDto.class, ObservableItem.class).addMappings(mapper -> {
            mapper.using(toObsItemType).map(ObservableItemPutDto::getObsItemTypeId, ObservableItem::setObsItemType);
        });
    }

}
