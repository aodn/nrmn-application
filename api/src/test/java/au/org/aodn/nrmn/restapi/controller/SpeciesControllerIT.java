package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.ObsItemType;
import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.model.db.ObsItemTypeTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class SpeciesControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private ObservableItemRepository observableItemRepository;

    @Autowired
    private ObsItemTypeTestData obsItemTypeTestData;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder().setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/species").addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter()).build();
    }
    /**
     * Read will work as before for this user
     */
    @Test
    @WithUserDetails("survey_editor@example.com")
    public void testPermissionOnNrmnSearchRead() {
        testNrmnSearchCommon();;
    }
    /**
     *
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testNrmnSearch() {
        testNrmnSearchCommon();;
    }

    protected void testNrmnSearchCommon() {
        ObservableItem species1 = getObservableItem("sea snake", "sea snakes");
        ObservableItem species2 = getObservableItem("sea snakes", "underwater bulldog");
        ObservableItem species3 = getObservableItem("underwater bulldog", null);

        given().spec(spec).auth().oauth2(jwtToken.get()).queryParam("species", "sea snak")
                .queryParam("searchType", "NRMN").queryParam("includeSuperseded", true).get().then().assertThat()
                .statusCode(200)
                .body("species", hasItems(species1.getObservableItemName(), species2.getObservableItemName()));

        given().spec(spec).auth().oauth2(jwtToken.get()).queryParam("species", "sea snak")
                .queryParam("searchType", "NRMN").queryParam("includeSuperseded", false).get().then().assertThat()
                .statusCode(200).body("species", hasItems());

        given().spec(spec).auth().oauth2(jwtToken.get()).queryParam("species", "underwater")
                .queryParam("searchType", "NRMN").queryParam("includeSuperseded", false).get().then().assertThat()
                .statusCode(200).body("species", hasItems(species3.getObservableItemName()));
    }

    private ObservableItem getObservableItem(String name, String supersededBy) {
        ObsItemType obsItemType = obsItemTypeTestData.persistedObsItemType();

        ObservableItem observableItem = ObservableItem.builder().obsItemType(obsItemType).observableItemName(name)
                .speciesEpithet(name).supersededBy(supersededBy).build();

        observableItemRepository.saveAndFlush(observableItem);
        return observableItem;
    }

}
