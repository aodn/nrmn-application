package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.ObsItemTypeTestData;
import au.org.aodn.nrmn.restapi.model.db.ObservableItemTestData;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class ObservableItemApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/reference/observableItem")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateObservableItem() {
        val obsItemType = obsItemTypeTestData.persistedObsItemType();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"observableItemName\": \"Lotella rhacina\"," +
                        "\"speciesEpithet\": \"verreauxi\"," +
                        "\"class\": \"ACTINOPTERYGII\"," +
                        "\"commonName\": \"Conger eel\"," +
                        "\"letterCode\": \"CVER\"," +
                        "\"obsItemTypeId\":"  + obsItemType.getObsItemTypeId()  +
                        "}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .body("observableItemName", is(equalTo("Lotella rhacina")))
                .body("speciesEpithet", is(equalTo("verreauxi")))
                .body("class", is(equalTo("ACTINOPTERYGII")))
                .body("commonName", is(equalTo("Conger eel")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testUpdateObservableItem() {
        val observableItem = observableItemTestData
                .defaultBuilder()
                .isInvertSized(true)
                .build();

        observableItemRepository.saveAndFlush(observableItem);

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"observableItemId\": \"" + observableItem.getObservableItemId() + "\"," +
                        "\"observableItemName\": \"" + observableItem.getObservableItemName() + "\"," +
                        "\"speciesEpithet\": \"" + observableItem.getObservableItemName() + "\"," +
                        "\"isInvertSized\": \"" + observableItem.getIsInvertSized() + "\"," +
                        "\"obsItemTypeId\":"  + observableItem.getObsItemType().getObsItemTypeId()  +
                        "}")
                .put(observableItem.getObservableItemId().toString())
                .then()
                .assertThat()
                .statusCode(200);

        val updatedItem = observableItemRepository.findById(observableItem.getObservableItemId());

        assertThat(updatedItem.get().getIsInvertSized(), equalTo(true));
    }
}
