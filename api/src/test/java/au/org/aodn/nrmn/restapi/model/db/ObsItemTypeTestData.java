package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObsItemTypeTestData {

    @Autowired
    private ObsItemTypeRepository obsItemTypeRepository;

    public ObsItemType persistedObsItemType() {
        val obsItemType = ObsItemType.builder()
            .obsItemTypeName("Species")
            .isActive(true)
            .build();
        obsItemTypeRepository.saveAndFlush(obsItemType);
        return obsItemType;
    }
}
