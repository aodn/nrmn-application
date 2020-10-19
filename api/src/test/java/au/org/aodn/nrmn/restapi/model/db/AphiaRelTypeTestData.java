package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.AphiaRelTypeRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AphiaRelTypeTestData {

    @Autowired
    private AphiaRelTypeRepository aphiaRelTypeRepository;

    public AphiaRelType persistedAphiaRelType() {
        val aphiaRelType = AphiaRelType.builder()
            .aphiaRelTypeId(1)
            .aphiaRelTypeName("is")
            .build();
        aphiaRelTypeRepository.saveAndFlush(aphiaRelType);
        return aphiaRelType;
    }
}
