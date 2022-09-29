package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem.ObservableItemBuilder;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;

@Component
public class ObservableItemTestData {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    private int observableItemNo = 0;

    public ObservableItem persistedObservableItem() {
        ObservableItem observableItem = defaultBuilder().build();
        return observableItemRepository.saveAndFlush(observableItem);
    }

    public ObservableItemBuilder defaultBuilder() {
        return ObservableItem.builder()
            .observableItemName("Observable item " + ++observableItemNo)
            .obsItemType(obsItemTypeTestData.persistedObsItemType())
            .speciesEpithet("Scomber scombrus");
    }
}
