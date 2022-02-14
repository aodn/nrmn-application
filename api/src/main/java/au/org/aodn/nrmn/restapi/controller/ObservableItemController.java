package au.org.aodn.nrmn.restapi.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemRow;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "observable items")
@RequestMapping(path = "/api/reference")
public class ObservableItemController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ModelMapper mapper;
    
    @GetMapping(path = "/observableItems")
    public ResponseEntity<List<ObservableItemRow>> list() {
        return ResponseEntity.ok(observableItemRepository.findAllProjectedBy());
    }

    @PostMapping("/observableItem")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservableItemDto newObservableItem(@Valid @RequestBody ObservableItemDto observableItemDto) {
        ObservableItem newObservableItem = mapper.map(observableItemDto, ObservableItem.class);
        validate(newObservableItem);
        ObservableItem persistedObservableItem = observableItemRepository.save(newObservableItem);
        return mapper.map(persistedObservableItem, ObservableItemDto.class);
    }
    
    @GetMapping("/observableItem/{id}")
    public ResponseEntity<ObservableItemGetDto> findOne(@PathVariable Integer id) {
        return observableItemRepository.findById(id).map(item -> ResponseEntity.ok(mapper.map(item, ObservableItemGetDto.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/observableItem/{id}")
    public ResponseEntity<Boolean> deleteOne(@PathVariable Integer id) {
        observableItemRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/observableItem/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ObservableItemGetDto updateObservableItem(@PathVariable Integer id,
                                                     @Valid @RequestBody ObservableItemPutDto observableItemPutDto) {
        ObservableItem observableItem = observableItemRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        // Only allow the species name to be changed within 72 hours of creation
        if(!observableItem.getObservableItemName().equals(observableItemPutDto.getObservableItemName())  &&
            (observableItem.getCreated() == null || ChronoUnit.HOURS.between(observableItem.getCreated(), LocalDateTime.now()) >= 72)) {
                List<ValidationError> errors = new ArrayList<ValidationError>();
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "observableItemName", "", "Species Name editing not allowed more than 72 hours after creation."));
                throw new ValidationException(errors);
        }

        mapper.map(observableItemPutDto, observableItem);
        validate(observableItem);
        ObservableItem persistedObservableItem = observableItemRepository.save(observableItem);
        return mapper.map(persistedObservableItem, ObservableItemGetDto.class);
    }

    private void validate(ObservableItem item) {

        List<ValidationError> errors = new ArrayList<ValidationError>();

        if(StringUtils.isEmpty(item.getObservableItemName()))
            errors.add(new ValidationError(ObservableItemDto.class.getName(), "observableItemName", item.getObservableItemName(), "Species Name Required."));

        ObservableItem probe = ObservableItem.builder().commonName(item.getCommonName()).letterCode(item.getLetterCode()).observableItemName(item.getObservableItemName()).build();
        Example<ObservableItem> example = Example.of(probe, ExampleMatcher.matchingAny());

        List<ObservableItem> allMatches = observableItemRepository.findAll(example);
        for(ObservableItem match: allMatches) {

            if(match.getObservableItemId().equals(item.getObservableItemId()))
                continue;

            if(StringUtils.isNotEmpty(match.getObservableItemName()) && match.getObservableItemName().equals(item.getObservableItemName()))
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "observableItemName", item.getObservableItemName(), "An item with this name already exists."));
            
            if(StringUtils.isNotEmpty(match.getLetterCode()) && match.getLetterCode().equals(item.getLetterCode()))
                errors.add(new ValidationError(ObservableItemDto.class.getName(), "letterCode", item.getLetterCode(), "An item with this letter code already exists."));
        }

        if(StringUtils.isEmpty(item.getSupersededBy())) {
            item.setSupersededBy(null);
        } else if(observableItemRepository.exactSearch(item.getSupersededBy()).isEmpty()){
            logger.info(String.format("Invalid supersededBy value: \"%s\". Setting to null.", item.getSupersededBy()));
            item.setSupersededBy(null);
        }

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
