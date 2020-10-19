package au.org.aodn.nrmn.restapi.model.db;

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
    private AphiaRefTestData aphiaRefTestData;

    @Autowired
    private AphiaRelTypeTestData aphiaRelTypeTestData;

    public ObservableItem persistedObservableItem() {
        val lengthWeight = LengthWeight.builder()
            .a(0.0281)
            .b(2.875)
            .cf(1.0)
            .sgfgu("F")
            .build();

        val obsItemAttribute = ImmutableMap.<String, String>builder()
            .put("Class", "Actinopterygii")
            .put("Genus", "Trimma")
            .put("Order", "Perciformes")
            .put("Family", "Gobiidae")
            .put("Phylum", "Chordata")
            .build();

        val observableItem = ObservableItem.builder()
            .observableItemName("Trimma sp. [sanguinellus]")
            .obsItemType(obsItemTypeTestData.persistedObsItemType())
            .aphiaRef(aphiaRefTestData.persistedAphiaRef())
            .aphiaRelType(aphiaRelTypeTestData.persistedAphiaRelType())
            .obsItemAttribute(obsItemAttribute)
            .build();

        observableItem.setLengthWeight(lengthWeight);  //TODO: look at modifying builder to synch child/parent objects
        observableItemRepository.saveAndFlush(observableItem);
        return observableItem;
    }
}
