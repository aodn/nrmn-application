package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.model.db.ObservableItemTestData;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class ObservableItemControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/reference/observableItems")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetObservableItemListItems() {
        ObservableItem testObservableItem = observableItemTestData.persistedObservableItem(observableItemTestData.defaultBuilder().build());

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .body("items.observableItemId",
                        hasItems(testObservableItem.getObservableItemId()));
    }
    /**
     * expect fail due to permission setting
     */
    @Test
    @WithUserDetails("survey_editor@example.com")
    public void testPermissionOnItemCreateOrUpdate() {

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("")           // Content isn't important as permission blocked before parsing body
                .post("observableItem")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("")           // Content isn't important as permission blocked before parsing body
                .put("observableItem/123")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
