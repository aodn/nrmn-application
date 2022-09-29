package au.org.aodn.nrmn.restapi.controller;

import static au.org.aodn.nrmn.restapi.dto.species.SpeciesSearchType.WORMS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.repository.HabitatGroupRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObsItemTypeRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.ReportGroupRepository;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemOptionsDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemTaxonomyDto;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesSearchType;
import au.org.aodn.nrmn.restapi.service.WormsService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Species")
@RequestMapping("/api/v1/species")
public class SpeciesController {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private HabitatGroupRepository habitatGroupRepository;

    @Autowired
    private ReportGroupRepository reportGroupRepository;

    @Autowired
    private ObsItemTypeRepository obsItemTypeRepository;

    @Autowired
    private WormsService wormsService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping
    public List<SpeciesDto> findSpecies(@RequestParam("species") String speciesName,
                                        @RequestParam("includeSuperseded") Boolean includeSuperseded,
                                        SpeciesSearchType searchType,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "50") int pageSize) {

        if (searchType.equals(WORMS)) {
            return wormsService.partialSearch(page, pageSize, speciesName)
                    .stream()
                    .map(aphiaRef -> mapper.map(aphiaRef, SpeciesDto.class))
                    .collect(Collectors.toList());
        } else {
            return observableItemRepository.fuzzySearch(PageRequest.of(page, pageSize), speciesName, includeSuperseded)
                    .stream()
                    .map(observableItem -> mapper.map(observableItem, SpeciesDto.class))
                    .collect(Collectors.toList());
        }
    }

    @GetMapping("taxonomyDetail")
    public ResponseEntity<ObservableItemOptionsDto> listTaxonomyDetails() {

        var observableItemOptions = new ObservableItemOptionsDto();

        var habitatGroups = habitatGroupRepository.findAll().stream()
                .filter(h -> !h.getName().isBlank())
                .map(h -> h.getName())
                .collect(Collectors.toList());
        observableItemOptions.setHabitatGroups(habitatGroups);

        var reportGroups = reportGroupRepository.findAll().stream()
                .filter(h -> !h.getName().isBlank())
                .map(h -> h.getName())
                .collect(Collectors.toList());
        observableItemOptions.setReportGroups(reportGroups);

        observableItemOptions.setObsItemTypes(obsItemTypeRepository.findAll());

        var allItems = observableItemRepository.findAll();
        observableItemOptions.setTaxonomy(ObservableItemTaxonomyDto.builder()
                .phylum(getUniqueListForValue(allItems.stream().map(ObservableItem::getPhylum)))
                .className(getUniqueListForValue(allItems.stream().map(ObservableItem::getClassName)))
                .order(getUniqueListForValue(allItems.stream().map(ObservableItem::getOrder)))
                .family(getUniqueListForValue(allItems.stream().map(ObservableItem::getFamily)))
                .genus(getUniqueListForValue(allItems.stream().map(ObservableItem::getGenus)))
                .speciesEpithet(getUniqueListForValue(allItems.stream().map(ObservableItem::getSpeciesEpithet)))
                .build());

        return ResponseEntity.ok(observableItemOptions);
    }

    private ArrayList<String> getUniqueListForValue(Stream<String> items) {
        /*
         * Filters out null values and returns a sorted distinct list
         */
        return items.filter(Objects::nonNull).distinct().sorted().collect(Collectors.toCollection(ArrayList::new));
    }
}
