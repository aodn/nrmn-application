package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverTestData {

    @Autowired
    private DiverRepository diverRepository;

    public Diver persistedDiver() {
        val diver = Diver.builder()
            .initials("RSS")
            .fullName("Rick Stuart-Smith")
            .build();
        diverRepository.saveAndFlush(diver);
        return diver;
    }
}
