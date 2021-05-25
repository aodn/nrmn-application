package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.dto.species.SpeciesSearchType;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.service.WormsService;
import au.org.aodn.nrmn.restapi.dto.species.SpeciesDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
}
