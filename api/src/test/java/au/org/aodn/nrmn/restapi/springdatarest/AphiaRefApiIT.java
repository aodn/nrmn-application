package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.AphiaRefTestData;
import au.org.aodn.nrmn.restapi.repository.AphiaRefRepository;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class AphiaRefApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private AphiaRefTestData aphiaRefTestData;

    @Autowired
    private AphiaRefRepository aphiaRefRepository;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/aphiaRefs")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testPostAphiaRefNotAllowed() {
        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "  \"aphiaId\": 1," +
                        "  \"url\": \"string\"," +
                        "  \"scientificName\": \"string\"," +
                        "  \"authority\": \"string\"," +
                        "  \"status\": \"string\"," +
                        "  \"isFreshwater\": true," +
                        "  \"isTerrestrial\": true," +
                        "  \"isExtinct\": true," +
                        "  \"modified\": \"2020-11-18T00:14:35.217Z\"" +
                        "}")
                .post()
                .then()
                .assertThat()
                .statusCode(405);
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetAphiaRef() {
        val aphiaRef = aphiaRefTestData.persistedAphiaRef();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .get("/" + aphiaRef.getAphiaId())
                .then()
                .assertThat()
                .statusCode(200)
                .body("aphiaId", equalTo(aphiaRef.getAphiaId()))
                .body("scientificName", equalTo(aphiaRef.getScientificName()));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetAphiaRefs() {
        val aphiaRef1 = aphiaRefTestData.persistedAphiaRef();
        val aphiaRef2 = aphiaRefTestData.defaultBuilder()
                                        .aphiaId(19999)
                                        .scientificName("species 2")
                                        .build();
        aphiaRefRepository.saveAndFlush(aphiaRef2);

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .body("_embedded.aphiaRefs.aphiaId", hasItems(aphiaRef1.getAphiaId(), aphiaRef2.getAphiaId()))
                .body("_embedded.aphiaRefs.scientificName", hasItems(aphiaRef1.getScientificName(),
                        aphiaRef2.getScientificName()));
    }
}
