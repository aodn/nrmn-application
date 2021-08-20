package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.AphiaRelType.AphiaRelTypeBuilder;
import au.org.aodn.nrmn.restapi.repository.AphiaRelTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AphiaRelTypeTestData {

    @Autowired
    private AphiaRelTypeRepository aphiaRelTypeRepository;

    private int aphiaRelTypeNo = 0;

    public AphiaRelType persistedAphiaRelType() {
        AphiaRelType aphiaRelType = defaultBuilder().build();
        aphiaRelTypeRepository.saveAndFlush(aphiaRelType);
        return aphiaRelType;
    }

    public AphiaRelTypeBuilder defaultBuilder() {
        return AphiaRelType.builder()
            .aphiaRelTypeId(1)
            .aphiaRelTypeName("Type " + ++aphiaRelTypeNo);
    }
}
