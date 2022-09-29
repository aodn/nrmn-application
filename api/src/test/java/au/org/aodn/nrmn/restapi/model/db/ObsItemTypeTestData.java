package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.ObsItemType;
import au.org.aodn.nrmn.restapi.data.model.ObsItemType.ObsItemTypeBuilder;
import au.org.aodn.nrmn.restapi.data.repository.ObsItemTypeRepository;

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
