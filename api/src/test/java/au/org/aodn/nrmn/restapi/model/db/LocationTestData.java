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
        return persistedLocation(location);
    }

    public Location persistedLocation(Location location ) {
        locationRepository.saveAndFlush(location);
        return location;
    }

    public Location buildWith(int itemNumber) {
        return Location.builder()
                .locationName("Location " + itemNumber)
                .isActive(true)
                .build();
    }

    public LocationBuilder defaultBuilder() {
        return Location.builder()
            .locationName("Location " + ++locationNo)
            .isActive(true);
    }
}
