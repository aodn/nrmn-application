package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Diver.DiverBuilder;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiverTestData {

    @Autowired
    private DiverRepository diverRepository;
    
    private int diverNo = 0;

    public Diver persistedDiver() {
        Diver diver = defaultBuilder().build();
        diverRepository.saveAndFlush(diver);
        return diver;
    }

    public Diver persistedDiver(Diver diver) {
        return diverRepository.saveAndFlush(diver);
    }

    public Diver buildWith(int itemNumber, String name) {
        return Diver.builder()
                .initials(String.valueOf(itemNumber))
                .fullName(name)
                .build();
    }

    public DiverBuilder defaultBuilder() {
        return Diver.builder()
            .initials("I" + ++diverNo)
            .fullName("Rick Stuart-Smith");
    }
}
