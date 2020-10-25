package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.ObsItemType.ObsItemTypeBuilder;
import au.org.aodn.nrmn.restapi.repository.ObsItemTypeRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObsItemTypeTestData {

    @Autowired
    private ObsItemTypeRepository obsItemTypeRepository;

    public ObsItemType persistedObsItemType() {
        val obsItemType = defaultBuilder().build();
        obsItemTypeRepository.saveAndFlush(obsItemType);
        return obsItemType;
    }

    public ObsItemTypeBuilder defaultBuilder() {
        return ObsItemType.builder()
            .obsItemTypeName("Species")
            .isActive(true);
    }
}
