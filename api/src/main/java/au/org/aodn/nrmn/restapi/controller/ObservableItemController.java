package au.org.aodn.nrmn.restapi.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemNodeDto;
import au.org.aodn.nrmn.restapi.service.ObservableItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reference Data - Observable Items")
@RequestMapping(path = "/api/v1/reference")
public class ObservableItemController {

    private static Logger logger = LoggerFactory.getLogger(ObservableItemController.class);

    @Autowired
    protected ObservableItemRepository observableItemRepository;

    @Autowired
    protected ObservableItemListRepository observableItemListRepository;

    @Autowired
    protected ModelMapper mapper;

    @Autowired
    protected ObjectMapper objMapper;

    @Autowired
    protected ObservableItemService observableItemService;

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
        observableItemService.validate(newObservableItem);
        ObservableItem persistedObservableItem = observableItemRepository.save(newObservableItem);
        return mapper.map(persistedObservableItem, ObservableItemDto.class);
    }

    @GetMapping("/observableItem/{id}")
    public ResponseEntity<ObservableItemGetDto> findOne(@PathVariable Integer id) {
        var obsItem = observableItemRepository.findById(id);
        return obsItem.map(item -> ResponseEntity.ok(mapper.map(item, ObservableItemGetDto.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    /**
     * Continue look up supersededBy until it goes to the root, then create a tree structure starting from the
     * this root and include all the node below.
     * @param id
     * @return
     */
    @GetMapping("/observableItem/{id}/family")
    public ResponseEntity<ObservableItemNodeDto> findRootOf(@PathVariable("id") Integer id) {
        Optional<ObservableItem> observableItem = observableItemRepository.findById(id);
        return observableItem
                .map(item -> ResponseEntity.ok(observableItemService.createForestOf(item)))
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

        return mapper.map(observableItemService.updateObservableItem(id, observableItemPutDto), ObservableItemGetDto.class);
    }

    @PutMapping("/observableItem/{id}/supersededBy")
    @ResponseStatus(HttpStatus.OK)
    public Integer updateSupersededByObservableItem(@PathVariable Integer id,
                                                     @Valid @RequestBody ObservableItemPutDto observableItemPutDto) throws InvocationTargetException, IllegalAccessException {

        return observableItemService.updateSupersededByObservableItem(id, observableItemPutDto);
    }

    @PutMapping("/observableItem/{id}/supersededByCascade")
    @ResponseStatus(HttpStatus.OK)
    public Integer updateSupersededCascadeByObservableItem(@PathVariable Integer id,
                                                           @Valid @RequestBody ObservableItemPutDto observableItemPutDto) throws InvocationTargetException, IllegalAccessException {

        return observableItemService.updateSupersededByObservableItemCascade(id, observableItemPutDto);
    }

    @PutMapping("/observableItem/{id}/superseded")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> updateSupersededObservableItem(@PathVariable Integer id,
                                                    @Valid @RequestBody ObservableItemPutDto observableItemPutDto) throws InvocationTargetException, IllegalAccessException {

        return observableItemService.updateSupersededObservableItem(id, observableItemPutDto);
    }

    @PutMapping("/observableItem/{id}/supersededCascade")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> updateSupersededObservableItemCascade(@PathVariable Integer id,
                                                        @Valid @RequestBody ObservableItemPutDto observableItemPutDto) throws InvocationTargetException, IllegalAccessException {

        return observableItemService.updateSupersededObservableItemCascade(id, observableItemPutDto);
    }
}
