package au.org.aodn.nrmn.restapi.controller.assembler;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemListItem;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemListItemAssembler {

    public ModelMapper mapper;

    private final EntityLinks entityLinks;

    @Autowired
    public ObservableItemListItemAssembler(ModelMapper mapper, EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
        this.mapper = mapper;
    }

    public ObservableItemListItem toModel(ObservableItem observableItem) {
        ObservableItemListItem observableItemListItem = mapper.map(observableItem, ObservableItemListItem.class);

        return observableItemListItem.add(entityLinks.linkToItemResource(ObservableItem.class,
         observableItemListItem.getObservableItemId()).withSelfRel());
    }
}
