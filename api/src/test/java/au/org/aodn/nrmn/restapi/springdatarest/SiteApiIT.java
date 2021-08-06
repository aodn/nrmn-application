package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
import au.org.aodn.nrmn.restapi.model.db.SiteTestData;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
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

import static au.org.aodn.nrmn.restapi.test.ApiUrl.entityRef;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
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
    @WithUserDetails("test@example.com")
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
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"," +
                        "\"locationId\": " + location.getLocationId() + "}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .path("siteId");

        val updatedSite = siteRepository.findById(siteId).get();

        assertThat(updatedSite.getSiteCode(), is(equalTo("TAS377")));
        assertThat(updatedSite.getCountry(), is(equalTo("Australia")));
        assertThat(updatedSite.getLocation().getLocationId(), is(equalTo(location.getLocationId())));
        assertThat(updatedSite.getState(), is(equalTo("Tasmania")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testPutSite() {
        val site = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"," +
                        "\"isActive\": false," +
                        "\"siteAttribute\": {" +
                        "    \"OldSiteCodes\": \"2102,7617\"," +
                        "    \"ProtectionStatus\": \"Fishing\"," +
                        "    \"ProxCountry\": \"Australia\"" +
                        "}," +
                         "\"locationId\": " + site.getLocation().getLocationId() + "}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(200);

        val updatedSite = siteRepository.findById(site.getSiteId()).get();

        assertThat(updatedSite.getSiteCode(), is(equalTo("TAS377")));
        assertThat(updatedSite.getSiteAttribute().get("OldSiteCodes"), is(equalTo("2102,7617")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateUsingExistingSiteCode() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + "\"," +
                        "\"siteName\": \"" + existingSite.getSiteName() + "\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"locationId\": " + existingSite.getLocation().getLocationId() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A site with this code already exists.")));
    }


    @Test
    @WithUserDetails("test@example.com")
    public void testCreateExactLatLong() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + " 123\"," +
                        "\"siteName\": \"" + existingSite.getSiteName() + " 123\"," +
                        "\"longitude\": " + existingSite.getLongitude() + "," +
                        "\"latitude\":" + existingSite.getLatitude() + "," +
                        "\"locationId\": " + existingSite.getLocation().getLocationId() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A site already exists within 200m of this location (" + existingSite.getSiteName() +" 0.00m)")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateCloseLat() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + " 123\"," +
                        "\"siteName\": \"" + existingSite.getSiteName() + " 123\"," +
                        "\"longitude\": " + existingSite.getLongitude() + "," +
                        "\"latitude\":" + (existingSite.getLatitude() - 0.001) + "," +
                        "\"locationId\": " + existingSite.getLocation().getLocationId() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", startsWith("A site already exists within 200m of this location"));
    }


    @Test
    @WithUserDetails("test@example.com")
    public void testCreateCloseLong() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + " 123\"," +
                        "\"siteName\": \"" + existingSite.getSiteName() + " 123\"," +
                        "\"longitude\": " + (existingSite.getLongitude() - 0.001) + "," +
                        "\"latitude\":" + existingSite.getLatitude() + "," +
                        "\"locationId\": " + existingSite.getLocation().getLocationId() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", startsWith("A site already exists within 200m of this location"));
    }


    @Test
    @WithUserDetails("test@example.com")
    public void testCreateUsingExistingSiteNameAtLocation() {
        val existingSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + existingSite.getSiteCode() + "#2" + "\"," +
                        "\"siteName\": \"" + existingSite.getSiteName() + "\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"locationId\": " + existingSite.getLocation().getLocationId() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A site with this name already exists in this location.")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testUpdateWithExistingSiteCode() {
        val site = siteTestData.persistedSite();
        val anotherSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + anotherSite.getSiteCode() + "\"," +
                        "\"siteName\": \"" + anotherSite.getSiteName() + "\"," +
                        "\"longitude\": " + site.getLongitude() + "," +
                        "\"latitude\": " + site.getLatitude() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"," +
                        "\"locationId\": " + site.getLocation().getLocationId() + "}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A site with this code already exists.")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testUpdateWithExistingSiteName() {
        val site = siteTestData.persistedSite();
        val anotherSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"" + site.getSiteCode()  + "\"," +
                        "\"siteName\": \"" + anotherSite.getSiteName() + "\"," +
                        "\"longitude\": " + site.getLongitude() + "," +
                        "\"latitude\": " + site.getLatitude() + "," +
                        "\"state\": \"Tasmania\"," +
                        "\"country\": \"Australia\"," +
                        "\"locationId\": " + anotherSite.getLocation().getLocationId() + "}")
                .put(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A site with this name already exists in this location.")));
    }

    @Test
    @WithUserDetails("test@example.com")
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

    @Test
    @WithUserDetails("power_user@example.com")
    public void testPowerUserCanGetSite() {
        val site = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .get(site.getSiteId().toString())
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @WithUserDetails("power_user@example.com")
    public void testPowerUserCantCreateSite() {
        val location = locationTestData.persistedLocation();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"siteCode\": \"TAS377\"," +
                        "\"siteName\": \"Low Islets\"," +
                        "\"longitude\": 147.7243," +
                        "\"latitude\": -40.13547," +
                        "\"location\": \"" + entityRef(port, "locations", location.getLocationId()) + "\"," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(403);
    }
}
