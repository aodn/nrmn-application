package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.assembler.ObservableItemListItemAssembler;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemListItem;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@Tag(name = "observable items")
public class ObservableItemController {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObservableItemListItemAssembler assembler;

    @GetMapping(path = "/api/observableItems")
    public CollectionModel<ObservableItemListItem> list() {
        return CollectionModel.of(
                observableItemRepository.findAll()
                              .stream()
                              .map(observableItem -> assembler.toModel(observableItem))
                              .collect(Collectors.toList())
        );
    }

}
