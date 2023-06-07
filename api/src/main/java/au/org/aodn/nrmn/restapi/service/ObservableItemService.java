package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.exception.ValidationException;
import au.org.aodn.nrmn.restapi.controller.validation.FormValidationError;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
}
