package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.LocationRepository;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
    pattern = ".*TestData"))
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("emptydb")
class LocationIT {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testMapping() {
        val location = locationTestData.persistedLocation();
        entityManager.clear();
        val persistedLocation = locationRepository.findById(location.getLocationId()).get();
        assertEquals(location, persistedLocation);
    }

}
