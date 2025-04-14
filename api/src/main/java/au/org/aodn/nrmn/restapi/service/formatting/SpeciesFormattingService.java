package au.org.aodn.nrmn.restapi.service.formatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.data.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.controller.mapping.StagedRowFormattedMapperConfig;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;

@Slf4j
@Service
public class SpeciesFormattingService {

    @Autowired
    ObservationRepository observationRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;
    
    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    public Collection<ObservableItem> getSpeciesForRows(Collection<StagedRow> rows) {
        var enteredSpeciesNames = rows.stream().map(StagedRow::getSpecies).collect(Collectors.toSet());
        return observableItemRepository.getAllSpeciesNamesMatching(enteredSpeciesNames);
    }

    public List<StagedRowFormatted> formatRowsWithSpecies(Collection<StagedRow> rows,
            Collection<ObservableItem> species) {

        Map<Long, StagedRow> rowMap = rows.stream().collect(Collectors.toMap(StagedRow::getId, r -> r));

        var speciesIds = species.stream()
                .mapToInt(ObservableItem::getObservableItemId)
                .toArray();

        var speciesAttributesMap = observationRepository
                .getSpeciesAttributesByIds(speciesIds).stream()
                .collect(Collectors.toMap(UiSpeciesAttributes::getSpeciesName, a -> a));

        var speciesMap = species.stream().collect(Collectors.toMap(ObservableItem::getObservableItemName, o -> o));
        var divers = new ArrayList<>(diverRepository.getAll());

        Map<String, Site> sites = siteRepository.getAllMap();
        var mapperConfig = new StagedRowFormattedMapperConfig();

        // 58716919 ns
        var mapper = mapperConfig.getModelMapper(speciesMap, rowMap, speciesAttributesMap, divers, sites);
        return rows
                .parallelStream()
                .map(stagedRow -> mapper.map(stagedRow, StagedRowFormatted.class))
                .collect(Collectors.toList());
    }
}
