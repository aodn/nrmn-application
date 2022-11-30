package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.*;
import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class SurveyControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private SurveyTestData surveyTestData;
    private String NON_EXISTENT_SURVEY_ID = "123123123";

    @Autowired
    private SurveyMethodTestData surveyMethodTestData;

    @Autowired
    private ObservationTestData observationTestData;

    @Autowired
    private DiverTestData diverTestData;

    @Autowired
    private ObservableItemTestData observableItemTestData;

    @Autowired
    private JwtToken jwtToken;

    RequestSpecification getDataSpec;

    @BeforeEach
    public void setup() {

        getDataSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/data/")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetSurveyList() {

        Survey testSurvey = surveyTestData.persistedSurvey();

        given()
                .spec(getDataSpec)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200)
                .body("lastRow", equalTo(1),
                        "items[0].surveyId", equalTo(testSurvey.getSurveyId()));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetSurvey() {

        Survey testSurvey = surveyTestData.persistedSurvey();

        given().spec(getDataSpec)
                .auth()
                .oauth2(jwtToken.get())
                .get("survey/" + testSurvey.getSurveyId().toString())
                .then()
                .assertThat()
                .statusCode(200)
                .body("surveyId", equalTo(testSurvey.getSurveyId()));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testNoSurvey() {

        Survey testSurvey = surveyTestData.persistedSurvey();

        assertFalse(testSurvey.getSurveyId().toString().equals(NON_EXISTENT_SURVEY_ID));

        given().spec(getDataSpec)
                .auth()
                .oauth2(jwtToken.get())
                .get("survey/" + NON_EXISTENT_SURVEY_ID)
                .then()
                .assertThat()
                .statusCode(404);
    }
    /**
     * Base test, all possible filters
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterBySurveyId() {

        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for(int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }

        surveyTestData.persistedSurvey(surveyList);

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"contains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(12, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(12, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().contains("12"));
        });

        // filter by surveyId not contains 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"notContains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(138, obj.get("lastRow"));  // Total 138

        items = obj.get("items");
        assertEquals(100, items.size());        // One page max 100
        items.forEach(item -> {
            assertFalse(item.get("surveyId").toString().contains("12"));
        });

        // filter by surveyId equals 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"equals\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(1, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(1, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().equals("12"));
        });

        // filter by surveyId not equals 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"notEqual\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(149, obj.get("lastRow"));  // Total not match

        items = obj.get("items");
        assertEquals(100, items.size());    // One page max 100 items
        items.forEach(item -> {
            assertFalse(item.get("surveyId").toString().equals("12"));
        });

        // filter by surveyId startsWith 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"startsWith\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(11, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(11, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().startsWith("12"));
        });

        // filter by surveyId endsWith 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"endsWith\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(2, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(2, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().endsWith("12"));
        });

        // filter by blank
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"blank\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(0, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(0, items.size());

        // filter by not blank
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"notBlank\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(150, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(100, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().trim().length() != 0);
        });

        // filter IN, not appear in GUI but use internally, case one , one item
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"in\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(1, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(1, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().trim().equals("12"));
        });

        // filter IN, not appear in GUI but use internally, case two, two items
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"in\",\"val\":\"12,14,\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(2, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(2, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().trim().equals("12") || item.get("surveyId").toString().trim().equals("14"));
        });
    }
    /**
     * This test diff from the testFilterBySurveyId() by applying two filters to the same field.
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterBySurveyIdCompositeAndPage() {

        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for(int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }

        surveyTestData.persistedSurvey(surveyList);

        // filter by surveyId contains 12 or 13
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"contains\",\"val\":\"12\"},{\"ops\":\"contains\",\"val\":\"13\"}]}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(24, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(24, items.size());
        items.forEach(item -> {
            assertTrue(item.get("surveyId").toString().contains("12") || item.get("surveyId").toString().contains("13") );
        });

        // filter by surveyId not contains 12 or 13 and go to page 1, basically this means everything and no need assert
        // individual items
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(150, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(50, items.size());        // One page max 100, total 150 -100 equals 50

        // filter by surveyId not contains 12 and 13 and go to page 1, basically this means skip anything 12 and 13
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyId\",\"ops\":\"AND\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(126, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(26, items.size());
        items.forEach(item -> {
            assertTrue(!item.get("surveyId").toString().contains("12") && !item.get("surveyId").toString().contains("13"));
        });
    }
    /**
     * Site require table join and date field need correct format
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterBySiteDate() {

        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }

        surveyTestData.persistedSurvey(surveyList);

        // filter by date and -12- which is apr since date format is yyyy-MM-dd
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyDate\",\"ops\":\"contains\",\"val\":\"-12-\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(31, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(31, items.size());

        items.forEach(item -> {
            LocalDate date = LocalDate.parse(item.get("surveyDate").toString());
            assertTrue(date.getMonth() == Month.DECEMBER);
        });

        // filter by day endsWith 12 or 13
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.surveyDate\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"endsWith\",\"val\":\"-12\"},{\"ops\":\"endsWith\",\"val\":\"-13\"}]}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(10, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(10, items.size());

        items.forEach(item -> {
            LocalDate date = LocalDate.parse(item.get("surveyDate").toString());
            assertTrue(date.getDayOfMonth() == 12 || date.getDayOfMonth() == 13);
        });
    }
    /**
     * Site require table join and date field need correct format
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByDepth() {

        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }
        surveyTestData.persistedSurvey(surveyList);

        // filter by depth 5. , depth is a combine of two fields in db
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"contains\",\"val\":\"5.\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(15, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(15, items.size());

        items.forEach(item -> {
            assertTrue(item.get("depth").toString(), item.get("depth").toString().contains("5."));
        });

        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"notContains\",\"val\":\"5.\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(135, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(100, items.size());

        boolean found = false;
        for(int i = 0; i < items.size(); i++) {
            // You should see this item because user do not want to contains 5. only
            if(items.get(i).get("depth").toString().contains("50.")) {
                found = true;
            }
        }
        assertTrue("Should see 50.", found);

        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"startsWith\",\"val\":\"5.\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(1, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(1, items.size());

        items.forEach(item -> {
            assertTrue(item.get("depth").toString(), item.get("depth").toString().startsWith("5."));
        });

        // Edge case test blank logic, although the depth field cannot be null
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"blank\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(0, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(0, items.size());

        // Edge case test contains just a dot, that is everything
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"contains\",\"val\":\".\"}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(150, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(50, items.size()); // Page 2

        // Test the part that after the dot
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.depth\",\"ops\":\"startsWith\",\"val\":\".1\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(0, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(0, items.size());

    }
    /**
     * More table join and sql operation is required for diver name
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByDiverName() {
        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }
        surveyTestData.persistedSurvey(surveyList);

        // Extra sample needed
        Diver d1 = diverTestData.persistedDiver(diverTestData.buildWith(1, "Apple orange"));
        Diver d2 = diverTestData.persistedDiver(diverTestData.buildWith(2, "car crossing"));
        Diver d3 = diverTestData.persistedDiver(diverTestData.buildWith(3, "n sugar"));
        Diver d4 = diverTestData.persistedDiver(diverTestData.buildWith(4, "b coconut"));
        Diver d5 = diverTestData.persistedDiver(diverTestData.buildWith(5, "leave"));

        for(int i = 99; i < 103; i++) {
            SurveyMethodEntity e = surveyMethodTestData.buildWith(surveyList.get(i), i);
            surveyMethodTestData.persistedSurveyMethod(e);

            if(i == 99) {
                Observation o = observationTestData.buildWith(e, d1, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 100) {
                Observation o = observationTestData.buildWith(e, d1, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d2, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 101) {
                Observation o = observationTestData.buildWith(e, d2, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d3, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d4, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 102) {
                Observation o = observationTestData.buildWith(e, d1, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d2, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d3, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, d5, i);
                observationTestData.persistedObservation(o);
            }
        }

        // filter by diver, expect "car crossing", "n sugar" hit
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.diverName\",\"ops\":\"contains\",\"val\":\"ar\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(3, obj.get("lastRow"));

        List<Map<String, Object>>  items = obj.get("items");
        assertEquals(3, items.size());

        items.forEach(item -> {
            // We should never see a name without ar
            assertFalse(item.get("diverName").toString(), !item.get("diverName").toString().contains("ar"));
        });

        // filter by diver, expect "n sugar" hit
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.diverName\",\"ops\":\"endsWith\",\"val\":\"ar\"}]")
                .queryParam("sort", "[{\"field\":\"survey.diverName\",\"order\":\"asc\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(2, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(2, items.size());

        // Since sorted by diver name we should expect this in order
        assertEquals(
                "Apple orange, car crossing, leave, n sugar",
                items.get(0).get("diverName").toString());

        assertEquals(
                "b coconut, car crossing, n sugar",
                items.get(1).get("diverName").toString());

        // Noted desc ordering in test, diver name equals "Apple orange" or ends with "ar"
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.diverName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"equals\",\"val\":\"Apple orange\"},{\"ops\":\"contains\",\"val\":\"ar\"}]}]")
                .queryParam("sort", "[{\"field\":\"survey.diverName\",\"order\":\"desc\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(4, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(4, items.size());

        // Since sorted by diver name we should expect this in order
        assertEquals(
                "b coconut, car crossing, n sugar",
                items.get(0).get("diverName").toString());

        assertEquals(
                "Apple orange, car crossing, leave, n sugar",
                items.get(1).get("diverName").toString());

        assertEquals(
                "Apple orange, car crossing",
                items.get(2).get("diverName").toString());

        assertEquals(
                "Apple orange",
                items.get(3).get("diverName").toString());

    }

    @Test
    @WithUserDetails("test@example.com")
    public void testFilterBySpecies() {
        // Generate a sample data of 150, the default page size on server is 100 hence we have two page of data.
        List<Survey> surveyList = new ArrayList<>();
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            surveyList.add(surveyTestData.buildWith(i));
        }
        surveyTestData.persistedSurvey(surveyList);

        ObservableItem oi1 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(1, "Species 1"));
        ObservableItem oi2 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(2, "Species 2"));
        ObservableItem oi3 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(3, "Species 3"));
        ObservableItem oi4 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(4, "Species 4"));
        ObservableItem oi5 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(5, "Species 5"));
        ObservableItem oi6 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(6, "Species 6"));
        ObservableItem oi7 = observableItemTestData.persistedObservableItem(observableItemTestData.buildWith(7, "Species 7"));

        for(int i = 100; i < 107; i++) {
            SurveyMethodEntity e = surveyMethodTestData.buildWith(surveyList.get(i), i);
            surveyMethodTestData.persistedSurveyMethod(e);

            if(i == 100) {
                Observation o = observationTestData.buildWith(e, null, oi1, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 101) {
                Observation o = observationTestData.buildWith(e, null, oi2, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi3, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 102) {
                Observation o = observationTestData.buildWith(e, null, oi3, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi4, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi5, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 103) {
                Observation o = observationTestData.buildWith(e, null, oi1, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi4, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi5, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi7, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 104) {
                Observation o = observationTestData.buildWith(e, null, oi6, i);
                observationTestData.persistedObservation(o);
            }
            else if(i == 105) {
                Observation o = observationTestData.buildWith(e, null, oi6, i);
                observationTestData.persistedObservation(o);

                o = observationTestData.buildWith(e, null, oi7, i);
                observationTestData.persistedObservation(o);
            }
        }

        // filter by species, expect "Species1" hit given the item id of oi1
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.observableItemId\",\"ops\":\"equals\",\"val\":\"" +  oi1.getObservableItemId() + "\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(2, obj.get("lastRow"));

        List<Map<String, Object>>  items = obj.get("items");
        assertEquals(2, items.size());

        assertEquals(101, items.get(0).get("surveyId"));
        assertEquals(104, items.get(1).get("surveyId"));

        // Noted desc ordering in test, composite filter and is always OR condition as in GUI
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"survey.observableItemId\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"equals\",\"val\":\"" + oi3.getObservableItemId() + "\"},{\"ops\":\"contains\",\"val\":\"" + oi6.getObservableItemId() + "\"}]}]")
                .queryParam("sort", "[{\"field\":\"survey.surveyId\",\"order\":\"desc\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("surveys")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");


        assertEquals(4, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(4, items.size());

        assertEquals(106, items.get(0).get("surveyId"));
        assertEquals(105, items.get(1).get("surveyId"));
        assertEquals(103, items.get(2).get("surveyId"));
        assertEquals(102, items.get(3).get("surveyId"));
    }
}
