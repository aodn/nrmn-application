package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.db.model.Location;
import au.org.aodn.nrmn.db.repository.LocationRepository;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
class LocationIT {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        Location location = locationTestData.persistedLocation();
        entityManager.clear();
        Location persistedLocation = locationRepository.findById(location.getLocationId()).get();
        assertEquals(location, persistedLocation);
    }

}
