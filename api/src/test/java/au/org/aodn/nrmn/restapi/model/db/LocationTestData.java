package au.org.aodn.nrmn.restapi.model.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Location;
import au.org.aodn.nrmn.restapi.data.model.Location.LocationBuilder;
import au.org.aodn.nrmn.restapi.data.repository.LocationRepository;

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
        return locationRepository.saveAndFlush(location);
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
