package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.DiverTestData;
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

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/divers")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateDiver() {
        given()
                .spec(spec)
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
    @WithUserDetails("test@gmail.com")
    public void testCreateUsingExistingInitials() {
        val existingDiver = diverTestData.persistedDiver();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"initials\": \"" + existingDiver.getInitials() + "\"," +
                        "\"fullName\": \"Avid Diver\"}")
                .post()
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A diver with those initials already exists.")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testUpdateUsingExistingInitials() {
        val diver = diverTestData.persistedDiver();
        val existingDiver = diverTestData.persistedDiver();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"initials\": \"" + existingDiver.getInitials() + "\"," +
                        "\"fullName\": \"Avid Diver\"}")
                .put(diver.getDiverId().toString())
                .then()
                .assertThat()
                .statusCode(400)
                .body("errors[0].message", is(equalTo("A diver with those initials already exists.")));
    }

}
