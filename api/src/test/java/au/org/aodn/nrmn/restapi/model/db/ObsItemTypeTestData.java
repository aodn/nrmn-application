package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.db.model.ObsItemType;
import au.org.aodn.nrmn.db.model.ObsItemType.ObsItemTypeBuilder;
import au.org.aodn.nrmn.db.repository.ObsItemTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObsItemTypeTestData {

    @Autowired
    private ObsItemTypeRepository obsItemTypeRepository;

    private int obsItemTypeNo = 0;

    public ObsItemType persistedObsItemType() {
        ObsItemType obsItemType = defaultBuilder().build();
        obsItemTypeRepository.saveAndFlush(obsItemType);
        return obsItemType;
    }

    public ObsItemTypeBuilder defaultBuilder() {
        return ObsItemType.builder()
                          .obsItemTypeName("Type " + ++obsItemTypeNo)
                          .isActive(true);
    }
}
