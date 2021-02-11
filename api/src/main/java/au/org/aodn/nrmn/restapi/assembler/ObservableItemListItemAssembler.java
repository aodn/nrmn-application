package au.org.aodn.nrmn.restapi.assembler;

import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemListItem;
import au.org.aodn.nrmn.restapi.model.db.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.Site;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemListItemAssembler {

    private final EntityLinks entityLinks;

    @Autowired
    public ObservableItemListItemAssembler(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    public ObservableItemListItem toModel(ObservableItem observableItem) {
        ObservableItemListItem observableItemListItem = ObservableItemListItem
                .builder()
                .observableItemId(observableItem.getObservableItemId())
                .observableItemName(observableItem.getObservableItemName())
                .commonName(observableItem.getCommonName())
                .supersededBy(observableItem.getSupersededBy())
                .phylum(observableItem.getPhylum())
                .clazz(observableItem.getClazz())
                .order(observableItem.getOrder())
                .family(observableItem.getFamily())
                .genus(observableItem.getGenus())
                .build();

        return observableItemListItem.add(entityLinks.linkToItemResource(Site.class, observableItemListItem.getObservableItemId()).withSelfRel());
    }
}
