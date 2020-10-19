package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTestData {

    @Autowired
    private LocationRepository locationRepository;

    public Location persistedLocation() {
        val location = Location.builder()
            .locationName("Central Caribbean")
            .isActive(true)
            .build();
        locationRepository.saveAndFlush(location);
        return location;
    }
}
