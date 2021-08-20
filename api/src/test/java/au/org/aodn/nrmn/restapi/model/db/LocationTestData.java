package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Location.LocationBuilder;
import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTestData {

    @Autowired
    private LocationRepository locationRepository;
    
    private int locationNo = 0;

    public Location persistedLocation() {
        Location location = defaultBuilder().build();
        locationRepository.saveAndFlush(location);
        return location;
    }

    public LocationBuilder defaultBuilder() {
        return Location.builder()
            .locationName("Location " + ++locationNo)
            .isActive(true);
    }
}
