package au.org.aodn.nrmn.restapi.controller.mapping;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.enums.Directions;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagedRowFormattedMapperConfig {

    Logger logger = LoggerFactory.getLogger(StagedRowFormattedMapperConfig.class);

    public ModelMapper getModelMapper(Map<String, ObservableItem> speciesMap, Map<Long, StagedRow> rowMap,
            Map<String, UiSpeciesAttributes> speciesAttributesMap, Collection<Diver> divers, Collection<Site> sites) {

        Converter<String, Diver> toDiver = ctx -> {
            if (ctx.getSource() == null)
                return null;
            Optional<Diver> diver = divers.stream()
                    .filter(d -> (StringUtils.isNotEmpty(d.getFullName()) && Normalizer
                            .normalize(d.getFullName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                            .equalsIgnoreCase(Normalizer.normalize(ctx.getSource(), Normalizer.Form.NFD)
                                    .replaceAll("[^\\p{ASCII}]", "")))
                            || (StringUtils.isNotEmpty(d.getInitials())
                                    && d.getInitials().equalsIgnoreCase(ctx.getSource())))
                    .findFirst();
            return diver.orElse(null);
        };

        Converter<Long, StagedRow> toRef = ctx -> rowMap.get(ctx.getSource());

        Converter<String, Optional<UiSpeciesAttributes>> toSpeciesAttribute = ctx -> {
            UiSpeciesAttributes speciesAttributes = speciesAttributesMap.get(ctx.getSource());
            return speciesAttributes != null ? Optional.of(speciesAttributes) : Optional.empty();
        };

        Converter<String, Site> toSite = ctx -> {
            if (ctx.getSource() == null)
                return null;
            Optional<Site> site = sites.stream()
                    .filter(d -> (StringUtils.isNotEmpty(d.getSiteCode())
                            && d.getSiteCode().equalsIgnoreCase(ctx.getSource())))
                    .findFirst();
            return site.orElse(null);
        };

        Converter<String, LocalDate> toDate = ctx -> {
            try {
                return LocalDate.parse(ctx.getSource(), TimeUtils.getRowDateFormatter());
            }
            catch (DateTimeParseException e) {
                logger.error("Fail to convert date time {} given format {} in toDate",
                        ctx.getSource(), TimeUtils.getRowDateFormatter().toString(), e);
                return null;
            }
        };

        Converter<String, Optional<LocalTime>> toTime = ctx -> TimeUtils.parseTime(ctx.getSource());

        Converter<String, Directions> toDirection = ctx -> {
            if(StringUtils.isEmpty(ctx.getSource()))
                return Directions.O;
            return EnumUtils.getEnumIgnoreCase(Directions.class,ctx.getSource());
        };

        Converter<String, Integer> toDepth = ctx -> {
            try {
                return Integer.parseInt(ctx.getSource().split("\\.")[0]);
            }
            catch (NumberFormatException e) {
                logger.error("Fail to parse [{}] to Integer in toDepth", ctx.getSource().split("\\.")[0], e);
                return null;
            }
        };

        Converter<String, Optional<Double>> toVis = ctx -> {
            double vis = NumberUtils.toDouble(ctx.getSource(), Double.MIN_VALUE);
            return (vis != Double.MIN_VALUE) ? Optional.of(vis) : Optional.empty();
        };

        Converter<String, Integer> toSurveyNum = ctx -> {
            String[] splitDepth = ctx.getSource().split("\\.");
            try {
                return splitDepth.length > 1 ? Integer.parseInt(splitDepth[1]) : 0;
            }
            catch (NumberFormatException e) {
                logger.error("Fail to parse [{}] to Integer in toSurveyNum", splitDepth[1], e);
                return null;
            }
        };

        Converter<String, Boolean> toInvertSizing = ctx -> StringUtils.isNotEmpty(ctx.getSource())
                ? ctx.getSource().equalsIgnoreCase("Yes")
                : false;

        Converter<String, Double> toDouble = ctx -> {
            double dbl = NumberUtils.toDouble(ctx.getSource(), Double.NaN);
            return Double.isNaN(dbl) ? null : dbl;
        };

        Converter<String, Integer> toInteger = ctx -> {
            try {
                int i = NumberUtils.toInt(ctx.getSource(), Integer.MIN_VALUE);
                return (i != Integer.MIN_VALUE) ? i : null;
            }
            catch (NumberFormatException e) {
                logger.error("Fail to parse [{}] to Integer in toInteger", ctx.getSource(), e);
                return null;
            }
        };

        Converter<String, Optional<ObservableItem>> toObservableItem = ctx -> {
            ObservableItem observableItem = speciesMap.get(ctx.getSource());
            return (observableItem != null) ? Optional.of(observableItem) : Optional.empty();
        };

        Converter<Map<Integer, String>, Map<Integer, Integer>> toMeasureJson = ctx -> {
            Map<Integer, Integer> measures = new HashMap<>();
            if (ctx.getSource() != null) {
                for (Map.Entry<Integer, String> entry : ctx.getSource().entrySet()) {
                    int val = NumberUtils.toInt(entry.getValue(), Integer.MIN_VALUE);
                    if (val != Integer.MIN_VALUE && val > 0)
                        measures.put(entry.getKey(), val);
                }
            }
            return measures;
        };

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(StagedRow.class, StagedRowFormatted.class).addMappings(mapper -> {

            mapper.using(toDiver).map(StagedRow::getDiver, StagedRowFormatted::setDiver);
            mapper.using(toDiver).map(StagedRow::getPqs, StagedRowFormatted::setPqs);

            mapper.using(toDouble).map(StagedRow::getLatitude, StagedRowFormatted::setLatitude);
            mapper.using(toDouble).map(StagedRow::getLongitude, StagedRowFormatted::setLongitude);

            mapper.using(toInteger).map(StagedRow::getBlock, StagedRowFormatted::setBlock);
            mapper.using(toInteger).map(StagedRow::getInverts, StagedRowFormatted::setInverts);
            mapper.using(toInteger).map(StagedRow::getMethod, StagedRowFormatted::setMethod);
            mapper.using(toInteger).map(StagedRow::getTotal, StagedRowFormatted::setTotal);

            mapper.using(toDate).map(StagedRow::getDate, StagedRowFormatted::setDate);
            mapper.using(toDepth).map(StagedRow::getDepth, StagedRowFormatted::setDepth);
            mapper.using(toDirection).map(StagedRow::getDirection, StagedRowFormatted::setDirection);
            mapper.using(toInvertSizing).map(StagedRow::getIsInvertSizing, StagedRowFormatted::setIsInvertSizing);
            mapper.using(toMeasureJson).map(StagedRow::getMeasureJson, StagedRowFormatted::setMeasureJson);
            mapper.using(toObservableItem).map(StagedRow::getSpecies, StagedRowFormatted::setSpecies);
            mapper.using(toRef).map(StagedRow::getId, StagedRowFormatted::setRef);
            mapper.using(toSite).map(StagedRow::getSiteCode, StagedRowFormatted::setSite);
            mapper.using(toSpeciesAttribute).map(StagedRow::getSpecies, StagedRowFormatted::setSpeciesAttributesOpt);
            mapper.using(toSurveyNum).map(StagedRow::getDepth, StagedRowFormatted::setSurveyNum);
            mapper.using(toTime).map(StagedRow::getTime, StagedRowFormatted::setTime);
            mapper.using(toVis).map(StagedRow::getVis, StagedRowFormatted::setVis);
        });
        return modelMapper;
    }
}
