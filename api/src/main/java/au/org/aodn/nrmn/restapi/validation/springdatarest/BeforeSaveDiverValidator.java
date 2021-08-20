package au.org.aodn.nrmn.restapi.validation.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeSaveDiverValidator")
public class BeforeSaveDiverValidator implements Validator {

    @Autowired
    private DiverRepository diverRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Diver.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        Diver diver = (Diver) object;
        Example<Diver> diverExample = Example.of(Diver.builder().initials(diver.getInitials()).build());
        Optional<Diver> existingDiverWithInitials = diverRepository.findOne(diverExample);

        if (existingDiverWithInitials.isPresent() && existingDiverWithInitials.get().getDiverId() != diver.getDiverId()) {
            errors.rejectValue("initials", "diver.initials.exists", "A diver with those initials already exists.");
        }
    }
}
