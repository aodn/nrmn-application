package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesSearchType;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.service.WormsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static au.org.aodn.nrmn.restapi.dto.species.SpeciesSearchType.WORMS;

@RestController
@Tag(name = "Species")
@RequestMapping("/api/species")
public class SpeciesController {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private WormsService wormsService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping
    public List<SpeciesDto> findSpecies(@RequestParam("species") String speciesName,
                                        @RequestParam("includeSuperseded") Boolean includeSuperseded,
                                        SpeciesSearchType searchType,
                                        @RequestParam(defaultValue = "0") int page) {
        if (searchType.equals(WORMS)) {
            return wormsService.partialSearch(page, speciesName)
            .stream()
            .map(aphiaRef -> mapper.map(aphiaRef, SpeciesDto.class))
            .collect(Collectors.toList());
        } else {
            return observableItemRepository.fuzzySearch(PageRequest.of(page, 50), speciesName, includeSuperseded)
            .stream()
            .map(observableItem -> mapper.map(observableItem, SpeciesDto.class))
            .collect(Collectors.toList());
        }
    }

    @GetMapping("taxonomyDetail")
    public ResponseEntity<HashMap<String, List<String>>> listTaxonomyDetails() {
        List<ObservableItem> allItems = observableItemRepository.findAll();

        // Group required fields into arrays with appropriate key
        HashMap<String, List<String>> dtoHash = new HashMap<>();
        dtoHash.put("phylum", getUniqueListForValue(allItems.stream().map(ObservableItem::getPhylum)));
        dtoHash.put("className", getUniqueListForValue(allItems.stream().map(ObservableItem::getClassName)));
        dtoHash.put("order", getUniqueListForValue(allItems.stream().map(ObservableItem::getOrder)));
        dtoHash.put("family", getUniqueListForValue(allItems.stream().map(ObservableItem::getFamily)));
        dtoHash.put("genus", getUniqueListForValue(allItems.stream().map(ObservableItem::getGenus)));
        dtoHash.put("speciesEpithet", getUniqueListForValue(allItems.stream().map(ObservableItem::getSpeciesEpithet)));

        return ResponseEntity.ok(dtoHash);
    }

    private ArrayList<String> getUniqueListForValue(Stream<String> items) {
        /*
        * Filters out null values and returns a sorted distinct list
        * */
        return items.filter(Objects::nonNull).distinct().sorted().collect(Collectors.toCollection(ArrayList::new));
    }
}
