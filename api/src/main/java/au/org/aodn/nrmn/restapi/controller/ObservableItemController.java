package au.org.aodn.nrmn.restapi.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.ObservationItemListView;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemListRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.FilterCondition;
import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import au.org.aodn.nrmn.restapi.controller.validation.FormValidationError;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reference Data - Observable Items")
@RequestMapping(path = "/api/v1/reference")
public class ObservableItemController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObservableItemListRepository observableItemListRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ObjectMapper objMapper;

    @GetMapping(path = "/observableItems")
    public  ResponseEntity<?> getObservationItemsWithFilters(@RequestParam(value = "sort", required = false) String sort,
                                                             @RequestParam(value = "filters", required = false) String filters,
                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) throws JsonProcessingException {

        // RequestParam do not support json object parsing automatically
        List<Filter> f = FilterCondition.parse(objMapper, filters, Filter[].class);
        List<Sorter> s = FilterCondition.parse(objMapper, sort, Sorter[].class);

        // Negative page size means you do not want page.
        Pageable pages = pageSize < 0 ? Pageable.unpaged() : PageRequest.of(page, pageSize);
        Page<ObservationItemListView> v = observableItemListRepository.findAllObservationItemBy(f, s, pages);
        Map<String, Object> data = new HashMap<>();

        data.put("lastRow", v.getTotalElements());
        data.put("items", v.getContent());

        return ResponseEntity.ok(data);

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
        Optional<ObservableItem> obsItem = observableItemRepository.findById(id);
        return obsItem.map(item -> ResponseEntity.ok(mapper.map(item, ObservableItemGetDto.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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

        var observableItem = observableItemRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observable Id " + id + " not found in updateObservableItem()"));

        // Only allow the species name to be changed within 72 hours of creation
        if (!observableItem.getObservableItemName().equals(observableItemPutDto.getObservableItemName()) &&
                (observableItem.getCreated() == null
                        || ChronoUnit.HOURS.between(observableItem.getCreated(), LocalDateTime.now()) >= 72)) {

            throw new ValidationException(ObservableItemDto.class.getName(),"observableItemName","Species Name editing not allowed more than 72 hours after creation.");
        }

        mapper.map(observableItemPutDto, observableItem);
        validate(observableItem);
        var persistedObservableItem = observableItemRepository.save(observableItem);
        return mapper.map(persistedObservableItem, ObservableItemGetDto.class);
    }

    private void validate(ObservableItem item) {

        List<FormValidationError> errors = new ArrayList<FormValidationError>();

        if (StringUtils.isEmpty(item.getObservableItemName()))
            errors.add(new FormValidationError(ObservableItemDto.class.getName(), "observableItemName",
                    item.getObservableItemName(), "Species Name Required."));

        ObservableItem probe = ObservableItem.builder().commonName(item.getCommonName())
                .letterCode(item.getLetterCode()).observableItemName(item.getObservableItemName()).build();
        Example<ObservableItem> example = Example.of(probe, ExampleMatcher.matchingAny());

        List<ObservableItem> allMatches = observableItemRepository.findAll(example);
        for (ObservableItem match : allMatches) {

            if (match.getObservableItemId().equals(item.getObservableItemId()))
                continue;

            if (StringUtils.isNotEmpty(match.getObservableItemName())
                    && match.getObservableItemName().equals(item.getObservableItemName()))
                errors.add(new FormValidationError(ObservableItemDto.class.getName(), "observableItemName",
                        item.getObservableItemName(), "An item with this name already exists."));

            if (StringUtils.isNotEmpty(match.getLetterCode()) && match.getLetterCode().equals(item.getLetterCode()))
                errors.add(new FormValidationError(ObservableItemDto.class.getName(), "letterCode", item.getLetterCode(),
                        "An item with this letter code already exists."));
        }

        if (StringUtils.isEmpty(item.getSupersededBy())) {
            item.setSupersededBy(null);
        } else if (observableItemRepository.exactSearch(item.getSupersededBy()).isEmpty()) {
            logger.info(String.format("Invalid supersededBy value: \"%s\". Setting to null.", item.getSupersededBy()));
            item.setSupersededBy(null);
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
