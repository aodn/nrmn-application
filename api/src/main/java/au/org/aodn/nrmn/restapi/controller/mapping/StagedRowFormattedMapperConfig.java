package au.org.aodn.nrmn.restapi.controller.mapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.util.TimeUtils;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@Configuration
public class StagedRowFormattedMapperConfig {

    @Autowired
    StagedRowFormattedMapperConfig(ObservableItemRepository observableItemRepository, StagedRowRepository stagedRowRepository,
            ObservationRepository observationRepository, DiverRepository diverRepository, SiteRepository siteRepository, ModelMapper modelMapper) {

        Converter<String, Diver> toDiver = ctx -> { List<Diver> divers = diverRepository.findByCriteria(ctx.getSource()); return divers.size() > 0 ? divers.get(0) : null;};
        Converter<Long, StagedRow> toRef = ctx -> stagedRowRepository.findById(ctx.getSource()).get();
        Converter<String, Optional<UiSpeciesAttributes>> toSpeciesAttributesOpt = ctx -> observationRepository.getSpeciesAttributesBySpeciesName(ctx.getSource());
        Converter<String, Site> toSite = ctx -> {
            List<Site> sites = siteRepository.findByCriteria(ctx.getSource());
            return sites.size() == 1 ? sites.get(0) : null;
        };
        Converter<String, LocalDate> toDate = ctx -> LocalDate.parse(ctx.getSource(), DateTimeFormatter.ofPattern("d/M/yyyy"));
        Converter<String, Optional<LocalTime>> toTime = ctx -> TimeUtils.parseTime(ctx.getSource());
        Converter<String, Integer> toDepth = ctx -> {
            try{
                return Integer.parseInt(ctx.getSource().split("\\.")[0]);
            }catch(NumberFormatException e){
                return null;
            }
        };
        Converter<String, Optional<Integer>> toVis = ctx -> {
            Integer vis = NumberUtils.toInt(ctx.getSource(), Integer.MIN_VALUE);
            return (vis == Integer.MIN_VALUE) ? Optional.of(vis) : Optional.empty();
        };
        Converter<String, Integer> toSurveyNum = ctx -> {
            String[] splitDepth = ctx.getSource().split("\\.");
            return splitDepth.length > 1 ? Integer.parseInt(splitDepth[1]) : null;
        };
        Converter<String, Boolean> toInvertSizing = ctx -> ctx.getSource() != null ? ctx.getSource().equalsIgnoreCase("YES") : false;

        Converter<String, Optional<ObservableItem>> toObservableItem = ctx -> {
            List<ObservableItem> result = observableItemRepository.findByCriteria(ctx.getSource());
            return (result.size() > 0) ? Optional.of(result.get(0)) : Optional.empty();
        };

        Converter<Map<Integer, String>, Map<Integer, Integer>> toMeasureJson = ctx -> {
            Map<Integer, Integer> measures = new HashMap<Integer, Integer>();
            for (Map.Entry<Integer, String> entry : ctx.getSource().entrySet()) {
                int val = NumberUtils.toInt(entry.getValue(), Integer.MIN_VALUE);
                if (val != Integer.MIN_VALUE)
                    measures.put(entry.getKey(), val);
            }

            return measures;
        };

        modelMapper.typeMap(StagedRow.class, StagedRowFormatted.class).addMappings(mapper -> {
            mapper.using(toObservableItem).map(StagedRow::getSpecies, StagedRowFormatted::setSpecies);
            mapper.using(toSpeciesAttributesOpt).map(StagedRow::getSpecies, StagedRowFormatted::setSpeciesAttributesOpt);
            mapper.using(toMeasureJson).map(StagedRow::getMeasureJson, StagedRowFormatted::setMeasureJson);
            mapper.using(toDiver).map(StagedRow::getDiver, StagedRowFormatted::setDiver);
            mapper.using(toDiver).map(StagedRow::getPqs, StagedRowFormatted::setPqs);
            
            mapper.using(toRef).map(StagedRow::getId, StagedRowFormatted::setRef);
            mapper.using(toSite).map(StagedRow::getSiteCode, StagedRowFormatted::setSite);
            mapper.using(toDate).map(StagedRow::getDate, StagedRowFormatted::setDate);
            mapper.using(toDepth).map(StagedRow::getDepth, StagedRowFormatted::setDepth);
            mapper.using(toSurveyNum).map(StagedRow::getDepth, StagedRowFormatted::setSurveyNum);
            mapper.using(toInvertSizing).map(StagedRow::getIsInvertSizing, StagedRowFormatted::setIsInvertSizing);
            mapper.using(toTime).map(StagedRow::getTime, StagedRowFormatted::setTime);
            mapper.using(toVis).map(StagedRow::getVis, StagedRowFormatted::setVis);
        });
    }
}
