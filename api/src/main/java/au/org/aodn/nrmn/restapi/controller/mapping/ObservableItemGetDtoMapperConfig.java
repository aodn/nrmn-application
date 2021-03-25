package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.model.db.AphiaRelType;
import au.org.aodn.nrmn.restapi.model.db.LengthWeight;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservableItemGetDtoMapperConfig {

    @Autowired
    ObservableItemGetDtoMapperConfig(ObsItemTypeRepository obsItemTypeRepository, ObservableItemRepository observableItemRepository, ModelMapper modelMapper) { 
        
        Converter<ObsItemType, String> toObsItemTypeName = ctx -> ctx.getSource() == null ? null : ctx.getSource().getObsItemTypeName();
        Converter<ObsItemType, Integer> toObsItemTypeId = ctx -> ctx.getSource() == null ? null : ctx.getSource().getObsItemTypeId();
        Converter<AphiaRelType, String> toAphiaRelTypeName = ctx -> ctx.getSource() == null ? null : ctx.getSource().getAphiaRelTypeName();
        Converter<Integer, String> toSupersededByIDs = ctx -> observableItemRepository.findOneProjectedById(ctx.getSource()).getSupersededIDs();
        Converter<Integer, String> toSupersededByNames = ctx -> observableItemRepository.findOneProjectedById(ctx.getSource()).getSupersededNames();
        Converter<LengthWeight, Double> toLengthWeightA = ctx -> ctx.getSource() == null ? null : ctx.getSource().getA();
        Converter<LengthWeight, Double> toLengthWeightB = ctx -> ctx.getSource() == null ? null : ctx.getSource().getB();
        Converter<LengthWeight, Double> toLengthWeightCf = ctx -> ctx.getSource() == null ? null : ctx.getSource().getCf();
        
        modelMapper.typeMap(ObservableItem.class, ObservableItemGetDto.class).addMappings(mapper -> {
            mapper.using(toObsItemTypeName).map(ObservableItem::getObsItemType, ObservableItemGetDto::setObsItemTypeName);
            mapper.using(toObsItemTypeId).map(ObservableItem::getObsItemType, ObservableItemGetDto::setObsItemTypeId);
            mapper.using(toAphiaRelTypeName).map(ObservableItem::getAphiaRelType, ObservableItemGetDto::setAphiaRelTypeName);
            mapper.using(toSupersededByIDs).map(ObservableItem::getObservableItemId, ObservableItemGetDto::setSupersededByIDs);
            mapper.using(toSupersededByNames).map(ObservableItem::getObservableItemId, ObservableItemGetDto::setSupersededByNames);
            mapper.using(toLengthWeightA).map(ObservableItem::getLengthWeight, ObservableItemGetDto::setLengthWeightA);
            mapper.using(toLengthWeightB).map(ObservableItem::getLengthWeight, ObservableItemGetDto::setLengthWeightB);
            mapper.using(toLengthWeightCf).map(ObservableItem::getLengthWeight, ObservableItemGetDto::setLengthWeightCf);
        });
    }

}
