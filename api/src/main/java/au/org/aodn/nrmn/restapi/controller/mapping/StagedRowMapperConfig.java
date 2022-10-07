package au.org.aodn.nrmn.restapi.controller.mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

public class StagedRowMapperConfig {
    
    public static ModelMapper GetModelMapper() {
        var modelMapper = new ModelMapper();

        var obsItemMapper = (Converter<Optional<ObservableItem>, String>) ctx -> {
            return ctx.getSource().isPresent() ? ctx.getSource().get().getObservableItemName() : null;
        };

        var dateMapper = (Converter<LocalDate, String>) ctx -> {
            return ctx.getSource() != null ? ctx.getSource().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null;
        };

        var integerMapper = (Converter<Optional<Double>, String>) ctx -> {
            return ctx.getSource().isPresent() ? String.valueOf(ctx.getSource().get().intValue()) : null;
        };

        var booleanMapper = (Converter<Boolean, String>) ctx -> {
            return ctx.getSource() != null && ctx.getSource() == true ? "Yes" : "No";
        };

        modelMapper.typeMap(StagedRowFormatted.class, StagedRow.class)
                .addMappings(mapper -> mapper.map(StagedRowFormatted::getMethod, StagedRow::setMethod))
                .addMappings(mapper -> mapper.map(StagedRowFormatted::getBlock, StagedRow::setBlock))
                .addMappings(mapper -> mapper.map(StagedRowFormatted::getDecimalDepth, StagedRow::setDepth))
                .addMappings(mapper -> mapper.map(src -> src.getDiver().getInitials(), StagedRow::setDiver))
                .addMappings(mapper -> mapper.using(dateMapper)
                        .map(StagedRowFormatted::getDate, StagedRow::setDate))
                .addMappings(mapper -> mapper.using(integerMapper)
                        .map(StagedRowFormatted::getVis, StagedRow::setVis))
                .addMappings(mapper -> mapper.using(booleanMapper)
                        .map(StagedRowFormatted::getIsInvertSizing, StagedRow::setIsInvertSizing))
                .addMappings(mapper -> mapper.using(obsItemMapper)
                        .map(StagedRowFormatted::getSpecies, StagedRow::setSpecies));

        return modelMapper;
    }
}
