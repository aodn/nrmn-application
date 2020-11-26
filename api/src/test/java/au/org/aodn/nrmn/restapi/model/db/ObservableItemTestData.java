package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.ObservableItem.ObservableItemBuilder;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObservableItemTestData {

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    @Autowired
    private LengthWeightTestData lengthWeightTestData;

    @Autowired
    private AphiaRefTestData aphiaRefTestData;

    @Autowired
    private AphiaRelTypeTestData aphiaRelTypeTestData;

    private int observableItemNo = 0;

    public ObservableItem persistedObservableItem() {
        val observableItem = defaultBuilder().build();
        observableItemRepository.saveAndFlush(observableItem);
        return observableItem;
    }

    public ObservableItemBuilder defaultBuilder() {
        return ObservableItem.builder()
            .observableItemName("Observable item " + ++observableItemNo)
            .lengthWeight(lengthWeightTestData.defaultBuilder().build())
            .obsItemType(obsItemTypeTestData.persistedObsItemType())
            .aphiaRef(aphiaRefTestData.persistedAphiaRef())
            .aphiaRelType(aphiaRelTypeTestData.persistedAphiaRelType())
            .obsItemAttribute(ImmutableMap.<String, String>builder()
                .put("Class", "Actinopterygii")
                .put("Genus", "Trimma")
                .put("Order", "Perciformes")
                .put("Family", "Gobiidae")
                .put("Phylum", "Chordata")
                .build());
    }
}
