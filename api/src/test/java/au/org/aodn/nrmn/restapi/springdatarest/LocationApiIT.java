package au.org.aodn.nrmn.restapi.springdatarest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class LocationApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification locationSpec;

    @BeforeEach
    public void setup() {

        locationSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/location")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateLocation() {
        given()
                .spec(locationSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"locationName\": \"Hawaii\"," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .body("locationName", is(equalTo("Hawaii")))
                .body("isActive", is(equalTo(true)));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateUsingExistingInitials() {
        Location existingDiver = locationTestData.persistedLocation();

        given()
                .spec(locationSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"locationName\": \"" + existingDiver.getLocationName() + "\"," +
                        "\"isActive\": true}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("[0].message", is(equalTo("A location with that name already exists.")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testUpdateUsingExistingInitials() {
        Location diver = locationTestData.persistedLocation();
        Location existingDiver = locationTestData.persistedLocation();

        given()
                .spec(locationSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"locationName\": \"" + existingDiver.getLocationName() + "\"," +
                        "\"isActive\": " + diver.getIsActive() + "}")
                .put(diver.getLocationId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("[0].message", is(equalTo("A location with that name already exists.")));
    }
}
