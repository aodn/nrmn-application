package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import au.org.aodn.nrmn.restapi.controller.validation.FormValidationError;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemGetDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemNodeDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import au.org.aodn.nrmn.restapi.util.ObjectUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Main logic related to operations for ObservableItem.
 */
@Service
public class ObservableItemService {

    private static Logger logger = LoggerFactory.getLogger(ObservableItemService.class);

    @Autowired
    protected ModelMapper mapper;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    protected BeanUtilsBean nullAwareBeanUtils = ObjectUtils.createNullAwareBeanUtils("observableItemId");

    public void validate(ObservableItem item) {

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
    /**
     * Update all superseded items, the code iterate all parents
     * @param id
     * @param newObservableItem
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Integer updateSupersededByObservableItemCascade(Integer id, ObservableItemPutDto newObservableItem) throws InvocationTargetException, IllegalAccessException {

        Integer lastId = id;
        Integer currentId;

        while (true) {
            currentId = updateSupersededByObservableItem(lastId, newObservableItem);
            if(!lastId.equals(currentId)) {
                lastId = currentId;
            }
            else {
                break;
            }
        }
        return currentId;
    }
    /**
     *
     * @param id
     * @param newObservableItem
     * @return The id of the superseded item
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Integer updateSupersededByObservableItem(Integer id, ObservableItemPutDto newObservableItem) throws InvocationTargetException, IllegalAccessException {
        // Get what is in db
        ObservableItem observableItem = observableItemRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observable Id " + id + " not found in updateSupersededByObservableItem()"));

        if(observableItem.getSupersededBy() != null) {
            ObservableItem supersededBy = observableItemRepository
                    .findByObservableItemName(observableItem.getSupersededBy())
                    .orElseThrow(() -> new ResourceNotFoundException("Observable Superseded Id " + id + " not found in updateSupersededByObservableItem()"));

            ObservableItemPutDto current = mapper.map(supersededBy, ObservableItemPutDto.class);

            // Copy non-null value and then call to save to db.
            nullAwareBeanUtils.copyProperties(current, newObservableItem);
            updateObservableItem(supersededBy.getObservableItemId(), current);

            return supersededBy.getObservableItemId();
        }
        else {
            return id;
        }
    }

    public ObservableItem updateObservableItem(Integer id, ObservableItemPutDto newObservableItem) {

        ObservableItem observableItem = observableItemRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observable Id " + id + " not found in updateObservableItem()"));

        // Only allow the species name to be changed within 72 hours of creation
        if (!observableItem.getObservableItemName().equals(newObservableItem.getObservableItemName()) &&
                (observableItem.getCreated() == null
                        || ChronoUnit.HOURS.between(observableItem.getCreated(), LocalDateTime.now()) >= 72)) {

            throw new ValidationException(ObservableItemDto.class.getName(),"observableItemName","Species Name editing not allowed more than 72 hours after creation.");
        }

        mapper.map(newObservableItem, observableItem);
        validate(observableItem);

        return observableItemRepository.save(observableItem);
    }

    public ObservableItemNodeDto createForestOf(ObservableItem i) {
        ObservableItem root = findRootOf(i);
        ObservableItemNodeDto r = new ObservableItemNodeDto();
        r.self = mapper.map(root, ObservableItemGetDto.class);
        return findChildrenOf(r);
    }

    protected ObservableItemNodeDto findChildrenOf(ObservableItemNodeDto i) {
        Optional<List<ObservableItem>> children = observableItemRepository.findBySupersededBy(i.self.getObservableItemName());
        if(children.isPresent()) {
            for(ObservableItem oi : children.get()) {
                ObservableItemNodeDto c = new ObservableItemNodeDto();
                c.parent = i.self;
                c.self = mapper.map(oi, ObservableItemGetDto.class);
                i.children.add(c);

                findChildrenOf(c);
            }
        }
        return i;
    }

    protected ObservableItem findRootOf(ObservableItem i) {
        if(i.getSupersededBy() != null && i.getSupersededBy().length() > 0) {
            // Recursive find parent
            Optional<ObservableItem> p = observableItemRepository.findByObservableItemName(i.getSupersededBy());
            return p.isPresent() ? findRootOf(p.get()) : p.get();
        }
        else {
            return i;
        }
    }
}
