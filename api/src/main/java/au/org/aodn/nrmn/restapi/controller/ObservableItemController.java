package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@RestController
@Tag(name = "observable items")
@RequestMapping(path = "/api/reference")
public class ObservableItemController {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ModelMapper mapper;
    
    @GetMapping(path = "/observableItems")
    public CollectionModel<ObservableItemRow> list() {
        return CollectionModel.of(
                observableItemRepository.findAllProjectedBy()
                .stream()
                .collect(Collectors.toList())
        );
    }

    @PostMapping("/observableItem")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservableItemDto newObservableItem(@Valid @RequestBody ObservableItemDto observableItemDto) {
        validatePost(observableItemDto);
        ObservableItem newObservableItem = mapper.map(observableItemDto, ObservableItem.class);
        ObservableItem persistedObservableItem = observableItemRepository.save(newObservableItem);
        return mapper.map(persistedObservableItem, ObservableItemDto.class);
    }

    private void validatePost(@RequestBody @Valid ObservableItemDto sitePostDto) {

        ObservableItem probe = new ObservableItem();
        
        probe.setCommonName(sitePostDto.getCommonName());
        probe.setLetterCode(sitePostDto.getLetterCode());
        probe.setObservableItemName(sitePostDto.getObservableItemName());

        Example<ObservableItem> example = Example.of(probe, ExampleMatcher.matchingAny());

        List<ValidationError> errors = new ArrayList<ValidationError>();
        List<ObservableItem> allMatches = observableItemRepository.findAll(example);
        for(ObservableItem match: allMatches) {
            if(match.getObservableItemName() != null && match.getObservableItemName().equals(sitePostDto.getObservableItemName()))
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "observableItemName", sitePostDto.getObservableItemName(), "An item with this name already exists."));
            
            if(match.getCommonName() != null && match.getCommonName().equals(sitePostDto.getCommonName()))
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "commonName", sitePostDto.getCommonName(), "An item with this common name already exists."));
            
            if(match.getLetterCode() != null && match.getLetterCode().equals(sitePostDto.getLetterCode()))
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "letterCode", sitePostDto.getLetterCode(), "An item with this letter code already exists."));
        }

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
