package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JavaType;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
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
    private JwtToken jwtToken;

    private ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    @WithUserDetails("test@example.com")
    public void testFilterBySurveyId() throws IOException {

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
    }
}
