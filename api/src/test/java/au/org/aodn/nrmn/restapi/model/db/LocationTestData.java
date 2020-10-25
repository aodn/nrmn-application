package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Location.LocationBuilder;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTestData {

    @Autowired
    private LocationRepository locationRepository;

    public Location persistedLocation() {
        val location = defaultBuilder().build();
        locationRepository.saveAndFlush(location);
        return location;
    }

    public LocationBuilder defaultBuilder() {
        return Location.builder()
            .locationName("Central Caribbean")
            .isActive(true);
    }
}
