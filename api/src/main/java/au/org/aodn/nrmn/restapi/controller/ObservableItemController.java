package au.org.aodn.nrmn.restapi.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemNodeDto;
import au.org.aodn.nrmn.restapi.service.ObservableItemService;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtilsBean;
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

    protected BeanUtilsBean nullCopyBeanUtils = ObjectUtils.createNullCopyBeanUtils(true);

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
    /**
     * Update multiple species in one transaction. The incoming items do not need to carry all fields, those missing fields
     * will be copied from values from db. Hence it allows you to update any field by just passing the fields you want to
     * update.
     *
     * @param observableItemPutDto - A list of species to be updated.
     * @return - List of updated species
     * @throws Exception - When any update species item id not found
     */
    @PutMapping("/observableItems")
    @Transactional
    public ResponseEntity<List<ObservableItemGetDto>> updateObservableItem(@Valid @RequestBody List<ObservableItemPutDto> observableItemPutDto) throws Exception {

        List<ObservableItemGetDto> result = new ArrayList<>();

        // Get the item and copy items to the incoming object when the incoming object field is null
        // hence we apply the changes to object.
        for(ObservableItemPutDto o : observableItemPutDto) {
            ResponseEntity k = findOne(o.getObservableItemId());

            if(k.getStatusCode() == HttpStatus.OK) {
                nullCopyBeanUtils.copyProperties(o, k.getBody());
                result.add(mapper.map(observableItemService.updateObservableItem(o.getObservableItemId(), o), ObservableItemGetDto.class));
            }
            else {
                throw new NoSuchElementException(String.format("Observation Item Id not found [%s]", o.getObservableItemId()));
            }
        }

        return ResponseEntity.ok(result);
    }
}
