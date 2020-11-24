package au.org.aodn.nrmn.restapi.validation.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeCreateDiverValidator")
public class BeforeCreateDiverValidator implements Validator {

    @Autowired
    private DiverRepository diverRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Diver.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        val diver = (Diver) object;

        if (diverRepository.findByCriteria(diver.getInitials())
                .isPresent()) {
            errors.rejectValue("initials", "diver.initials.exists", "a diver with those initials already exists");
        }
    }
}
