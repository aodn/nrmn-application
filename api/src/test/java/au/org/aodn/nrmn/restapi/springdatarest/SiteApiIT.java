package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
import au.org.aodn.nrmn.restapi.model.db.SiteTestData;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SiteApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/sites")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testPostSite() {
        val location = locationTestData.persistedLocation();

        Integer siteId = given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"location\": \"http://localhost:" + port + "/api/locations/" + location.getLocationId() +
                        "\"," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"State\": \"Tasmania\"," +
                        "    \"Country\": \"Australia\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("siteId");

        val persistedSite = siteRepository.findById(siteId)
                .get();

        assertThat(persistedSite.getSiteCode(), is(equalTo("TAS377")));
        assertThat(persistedSite.getSiteAttribute()
                .get("OldSiteCodes"), is(equalTo("2102,7617")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateWithExistingSite() {
        val existingSite = siteTestData.defaultBuilder()
                .siteCode("TES001")
                .build();
        siteRepository.save(existingSite);

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"location\": \"http://localhost:" + port + "/api/locations/" + existingSite.getLocation()
                        .getLocationId() +
                        "\"," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"State\": \"Tasmania\"," +
                        "    \"Country\": \"Australia\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("siteId");
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testUpdateWithExistingSite() {
        val site = siteTestData.persistedSite();
        val location = locationTestData.persistedLocation();
        
        val existingSite = siteTestData.defaultBuilder()
                .siteCode("TES001")
                .location(location)
                .build();
        siteRepository.save(existingSite);

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TES001\"," +
                        "\"siteName\": \"" + site.getSiteName() + "\"," +
                        "\"longitude\": " + site.getLongitude() + "," +
                        "\"latitude\": " + site.getLatitude() + "," +
                        "\"location\": \"http://localhost:" + port + "/api/locations/" +
                        site.getLocation().getLocationId() + "\"," +
                        "\"isActive\": " + site.getIsActive() + "}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("a site with that code already exists")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateWithMissingValues() {
        val existingSite = siteTestData.defaultBuilder()
                .siteCode("TES001")
                .build();
        siteRepository.save(existingSite);

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors.property", hasItems("siteCode", "siteName", "longitude", "latitude", "location"))
                .body("errors.message", contains("must not be null", "must not be null", "must not be null",
                        "must not be null", "must not be null"));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testDeleteSite() {
        val site = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .delete(site.getSiteId()
                        .toString())
                .then()
                .assertThat()
                .statusCode(204);

        val persistedSite = siteRepository.findById(site.getSiteId());

        assertFalse(persistedSite.isPresent());
    }
}
