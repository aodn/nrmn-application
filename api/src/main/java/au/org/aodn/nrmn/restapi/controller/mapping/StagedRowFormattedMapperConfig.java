package au.org.aodn.nrmn.restapi.controller.mapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

public class StagedRowFormattedMapperConfig {

    public ModelMapper getModelMapper(Map<String, ObservableItem> speciesMap, Map<Long, StagedRow> rowMap,
            Map<String, UiSpeciesAttributes> speciesAttributesMap, Collection<Diver> divers, Collection<Site> sites) {

        Converter<String, Diver> toDiver = ctx -> {
            if (ctx.getSource() == null)
                return null;
            Optional<Diver> diver = divers.stream()
                    .filter(d -> (d.getFullName() != null && d.getFullName().equalsIgnoreCase(ctx.getSource()))
                            || (d.getInitials() != null && d.getInitials().equalsIgnoreCase(ctx.getSource())))
                    .findFirst();
            return diver.isPresent() ? diver.get() : null;
        };

        Converter<Long, StagedRow> toRef = ctx -> rowMap.get(ctx.getSource());

        Converter<String, Optional<UiSpeciesAttributes>> toSpeciesAttributesOpt = ctx -> {
            UiSpeciesAttributes speciesAttributes = speciesAttributesMap.get(ctx.getSource());
            return speciesAttributes != null ? Optional.of(speciesAttributes) : Optional.empty();
        };

        Converter<String, Site> toSite = ctx -> {
            if (ctx.getSource() == null)
                return null;
            Optional<Site> site = sites.stream()
                    .filter(d -> (d.getSiteCode() != null && d.getSiteCode().equalsIgnoreCase(ctx.getSource())))
                    .findFirst();
            return site.isPresent() ? site.get() : null;
        };

        Converter<String, LocalDate> toDate = ctx -> {
            try {
                return LocalDate.parse(ctx.getSource(), DateTimeFormatter.ofPattern("d/M/yyyy"));
            } catch (DateTimeParseException e) {
                return null;
            }
        };

        Converter<String, Optional<LocalTime>> toTime = ctx -> TimeUtils.parseTime(ctx.getSource());

        Converter<String, Directions> toDirection = ctx -> EnumUtils.getEnumIgnoreCase(Directions.class, ctx.getSource().trim());

        Converter<String, Integer> toDepth = ctx -> {
            try {
                return Integer.parseInt(ctx.getSource().split("\\.")[0]);
            } catch (NumberFormatException e) {
                return null;
            }
        };

        Converter<String, Optional<Double>> toVis = ctx -> {
            Double vis = NumberUtils.toDouble(ctx.getSource(), Double.MIN_VALUE);
            return (vis != Double.MIN_VALUE) ? Optional.of(vis) : Optional.empty();
        };

        Converter<String, Integer> toSurveyNum = ctx -> {
            String[] splitDepth = ctx.getSource().split("\\.");
            try {
                return splitDepth.length > 1 ? Integer.parseInt(splitDepth[1]) : 0;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        Converter<String, Boolean> toInvertSizing = ctx -> ctx.getSource() != null
                ? ctx.getSource().equalsIgnoreCase("YES")
                : false;

        Converter<String, Double> toDouble = ctx -> {
            Double dbl = NumberUtils.toDouble(ctx.getSource(), Double.NaN);
            return (dbl != Double.NaN) ? dbl : null;
        };

        Converter<String, Integer> toInteger = ctx -> {
            try {
                Integer i = NumberUtils.toInt(ctx.getSource(), Integer.MIN_VALUE);
                return (i != Integer.MIN_VALUE) ? i : null;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        Converter<String, Optional<ObservableItem>> toObservableItem = ctx -> {
            ObservableItem observableItem = speciesMap.get(ctx.getSource());
            return (observableItem != null) ? Optional.of(observableItem) : Optional.empty();
        };

        Converter<Map<Integer, String>, Map<Integer, Integer>> toMeasureJson = ctx -> {
            Map<Integer, Integer> measures = new HashMap<Integer, Integer>();
            if (ctx.getSource() != null) {
                for (Map.Entry<Integer, String> entry : ctx.getSource().entrySet()) {
                    int val = NumberUtils.toInt(entry.getValue(), Integer.MIN_VALUE);
                    if (val != Integer.MIN_VALUE)
                        measures.put(entry.getKey(), val);
                }
            }
            return measures;
        };

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(StagedRow.class, StagedRowFormatted.class).addMappings(mapper -> {
            mapper.using(toObservableItem).map(StagedRow::getSpecies, StagedRowFormatted::setSpecies);
            mapper.using(toSpeciesAttributesOpt).map(StagedRow::getSpecies,
                    StagedRowFormatted::setSpeciesAttributesOpt);
            mapper.using(toMeasureJson).map(StagedRow::getMeasureJson, StagedRowFormatted::setMeasureJson);
            mapper.using(toDiver).map(StagedRow::getDiver, StagedRowFormatted::setDiver);
            mapper.using(toDirection).map(StagedRow::getDirection, StagedRowFormatted::setDirection);
            mapper.using(toDiver).map(StagedRow::getPqs, StagedRowFormatted::setPqs);
            mapper.using(toDouble).map(StagedRow::getLatitude, StagedRowFormatted::setLatitude);
            mapper.using(toDouble).map(StagedRow::getLongitude, StagedRowFormatted::setLongitude);
            mapper.using(toRef).map(StagedRow::getId, StagedRowFormatted::setRef);
            mapper.using(toSite).map(StagedRow::getSiteCode, StagedRowFormatted::setSite);
            mapper.using(toDate).map(StagedRow::getDate, StagedRowFormatted::setDate);
            mapper.using(toDepth).map(StagedRow::getDepth, StagedRowFormatted::setDepth);
            mapper.using(toInteger).map(StagedRow::getMethod, StagedRowFormatted::setMethod);
            mapper.using(toInteger).map(StagedRow::getInverts, StagedRowFormatted::setInverts);
            mapper.using(toSurveyNum).map(StagedRow::getDepth, StagedRowFormatted::setSurveyNum);
            mapper.using(toInvertSizing).map(StagedRow::getIsInvertSizing, StagedRowFormatted::setIsInvertSizing);
            mapper.using(toTime).map(StagedRow::getTime, StagedRowFormatted::setTime);
            mapper.using(toVis).map(StagedRow::getVis, StagedRowFormatted::setVis);
        });
        return modelMapper;
    }
}
