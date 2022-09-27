package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.db.model.Diver;
import au.org.aodn.nrmn.restapi.model.db.DiverTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class DiverApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private DiverTestData diverTestData;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification diverSpec;

    private RequestSpecification diversSpec;

    @BeforeEach
    public void setup() {
        diverSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/diver")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();

        diversSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/divers")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateDiver() {
        given()
                .spec(diverSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"initials\": \"AVD\"," +
                        "\"fullName\": \"Avid Diver\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .body("initials", is(equalTo("AVD")))
                .body("fullName", is(equalTo("Avid Diver")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testCreateUsingExistingInitials() {
        Diver existingDiver = diverTestData.persistedDiver();

        given()
                .spec(diverSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"initials\": \"" + existingDiver.getInitials() + "\"," +
                        "\"fullName\": \"Avid Diver\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("[0].message", is(equalTo("A diver already has these initials.")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testUpdateUsingExistingInitials() {
        Diver diver = diverTestData.persistedDiver();
        Diver existingDiver = diverTestData.persistedDiver();

        given().spec(diversSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("[{\"diverId\": " + diver.getDiverId() +
                        ",\"initials\": \"" + existingDiver.getInitials() +
                        "\",\"fullName\": \"Avid Diver\"}]")
                .put()
                .then()
                .assertThat()
                .statusCode(400)
                .body("[0].message", is(equalTo("A diver already has these initials.")));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testUpdateUsingExistingFullName() {
        Diver diver = diverTestData.persistedDiver();
        Diver existingDiver = diverTestData.persistedDiver();

        given()
                .spec(diversSpec)
                .auth()
                .oauth2(jwtToken.get())
                .body("[{\"diverId\": " + diver.getDiverId() +
                        ",\"initials\": \"AVD\"" +
                        ",\"fullName\": \"" + existingDiver.getFullName() + "\"}]")
                .put()
                .then()
                .assertThat()
                .statusCode(400)
                .body("[0].message", is(equalTo("A diver with the same name already exists.")));
    }

}
