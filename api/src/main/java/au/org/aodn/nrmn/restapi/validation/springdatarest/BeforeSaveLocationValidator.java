package au.org.aodn.nrmn.restapi.validation.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeSaveLocationValidator")
public class BeforeSaveLocationValidator implements Validator {

    @Autowired
    private LocationRepository locationRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return Location.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        val location = (Location) object;

        val existingLocationWithName = locationRepository.findByLocationName(location.getLocationName());

        if (existingLocationWithName.isPresent()
                && !existingLocationWithName.get().getLocationId().equals(location.getLocationId())) {
            errors.rejectValue("locationName", "location.locationName.exists",
                    "a location with that name already exists");
        }
    }
}
