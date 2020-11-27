package au.org.aodn.nrmn.restapi.springdatarest;

import au.org.aodn.nrmn.restapi.model.db.AphiaRefTestData;
import au.org.aodn.nrmn.restapi.model.db.AphiaRelTypeTestData;
import au.org.aodn.nrmn.restapi.model.db.ObsItemTypeTestData;
import au.org.aodn.nrmn.restapi.model.db.ObservableItemTestData;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;

import static au.org.aodn.nrmn.restapi.test.ApiUrl.entityRef;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ObservableItemApiIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    @Autowired
    private AphiaRefTestData aphiaRefTestData;

    @Autowired
    private AphiaRelTypeTestData aphiaRelTypeTestData;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/observableItems")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateObservableItem() {
        val obsItemType = obsItemTypeTestData.persistedObsItemType();
        val aphiaRef = aphiaRefTestData.persistedAphiaRef();
        val aphiaRelType = aphiaRelTypeTestData.persistedAphiaRelType();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"observableItemName\": \"Lotella rhacina\"," +
                        "\"obsItemAttribute\": {" +
                        "    \"Class\": \"Actinopterygii\"," +
                        "    \"Genus\": \"Conger\"," +
                        "    \"Order\": \"Anguilliformes\"," +
                        "    \"Family\": \"Congridae\"," +
                        "    \"Phylum\": \"Chordata\"," +
                        "    \"MaxLength\": 200," +
                        "    \"CommonName\": \"Conger eel\"," +
                        "    \"LetterCode\": \"CVER\"," +
                        "    \"OtherGroups\": \"Higher carnivore (including piscivore)\"," +
                        "    \"SpeciesEpithet\": \"verreauxi\"}," +
                        "\"lengthWeight\": {" +
                        "    \"a\": 0.0017," +
                        "    \"b\": 3.145," +
                        "    \"cf\": 1," +
                        "    \"sgfgu\": \"Gu\"}," +
                        "\"obsItemType\": \"" + entityRef(port, "obsItemTypes", obsItemType.getObsItemTypeId()) +
                        "\"," +
                        "\"aphiaRef\": \"" + entityRef(port, "aphiaRefs", aphiaRef.getAphiaId()) + "\"," +
                        "\"aphiaRefType\": \"" + entityRef(port, "aphiaRefTypes", aphiaRelType.getAphiaRelTypeId()) +
                        "\"" +
                        "}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .body("observableItemName", is(equalTo("Lotella rhacina")))
                .body("obsItemAttribute.CommonName", is(equalTo("Conger eel")))
                .body("lengthWeight.b", is(equalTo(3.145f)));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testCreateObservableItemNoLengthWeight() {
        val obsItemType = obsItemTypeTestData.persistedObsItemType();
        val aphiaRef = aphiaRefTestData.persistedAphiaRef();
        val aphiaRelType = aphiaRelTypeTestData.persistedAphiaRelType();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"observableItemName\": \"Lotella rhacina\"," +
                        "\"obsItemType\": \"" + entityRef(port, "obsItemTypes", obsItemType.getObsItemTypeId()) +
                        "\"," +
                        "\"aphiaRef\": \"" + entityRef(port, "aphiaRefs", aphiaRef.getAphiaId()) + "\"," +
                        "\"aphiaRefType\": \"" + entityRef(port, "aphiaRefTypes", aphiaRelType.getAphiaRelTypeId()) +
                        "\"" +
                        "}")
                .post()
                .then()
                .assertThat()
                .statusCode(201)
                .body("observableItemName", is(equalTo("Lotella rhacina")));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testUpdateObservableItemLengthWeight() {
        val observableItem = observableItemRepository.save(
                observableItemTestData.defaultBuilder()
                                      .lengthWeight(null)
                                      .obsItemAttribute(null)
                                      .build());

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("{" +
                        "\"observableItemName\": \"" + observableItem.getObservableItemName() + "\"," +
                        "\"obsItemType\": \"" + entityRef(port, "obsItemTypes", observableItem.getObsItemType().getObsItemTypeId()) +
                        "\"," +
                        "\"lengthWeight\": {" +
                        "    \"observableItemId\": " + observableItem.getObservableItemId() + "," +
                        "    \"a\": 0.0017," +
                        "    \"b\": 3.145," +
                        "    \"cf\": 1," +
                        "    \"sgfgu\": \"Gu\"," +
                        "    \"observableItem\": \"" + entityRef(port, "observableItems",
                         observableItem.getObservableItemId()) + "\"}," +
                        "\"aphiaRef\": \"" + entityRef(port, "aphiaRefs", observableItem.getAphiaRef().getAphiaId()) + "\"," +
                        "\"aphiaRefType\": " +
                        "    \"" + entityRef(port, "aphiaRefTypes", observableItem.getAphiaRelType().getAphiaRelTypeId()) +
                        "\"" +
                        "}")
                .put(observableItem.getObservableItemId().toString())
                .then()
                .assertThat()
                .statusCode(200);
    }
}
