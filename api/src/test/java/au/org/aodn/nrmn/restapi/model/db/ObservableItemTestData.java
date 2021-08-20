package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem.ObservableItemBuilder;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemTestData {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    private int observableItemNo = 0;

    public ObservableItem persistedObservableItem() {
        ObservableItem observableItem = defaultBuilder().build();
        observableItemRepository.saveAndFlush(observableItem);
        return observableItem;
    }

    public ObservableItemBuilder defaultBuilder() {
        return ObservableItem.builder()
            .observableItemName("Observable item " + ++observableItemNo)
            .obsItemType(obsItemTypeTestData.persistedObsItemType())
            .speciesEpithet("Scomber scombrus");
    }
}
