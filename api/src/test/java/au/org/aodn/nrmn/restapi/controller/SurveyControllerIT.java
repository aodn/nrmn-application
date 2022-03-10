package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.SurveyTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithUserDetails;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
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

    RequestSpecification getDataSpec;

    @BeforeEach
    public void setup() {

        getDataSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/data/")
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
                .body("surveyId", hasItems(testSurvey.getSurveyId()));
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

    // Tests are commented due to the use of ep_site_list

    // @Test
    // @WithUserDetails("test@example.com")
    // public void testFilterSurveys() {

    // val testSurvey = surveyTestData.persistedSurvey();

    // given().spec(getDataSpec)
    // .auth()
    // .oauth2(jwtToken.get())
    // .get("surveys?surveyId=" + testSurvey.getSurveyId())
    // .then()
    // .assertThat()
    // .body("[0].surveyId", equalTo(testSurvey.getSurveyId()));
    // }

    // @Test
    // @WithUserDetails("test@example.com")
    // public void testFilterSurveysForSite() {

    // val testSurvey = surveyTestData.persistedSurvey();
    // val siteId = testSurvey.getSite().getSiteId();

    // given().spec(getDataSpec)
    // .auth()
    // .oauth2(jwtToken.get())
    // .get(queryPreamble + "&siteId=" + siteId)
    // .then()
    // .assertThat()
    // .body("[0].surveyId", equalTo(testSurvey.getSurveyId()));

    // given().spec(getDataSpec)
    // .auth()
    // .oauth2(jwtToken.get())
    // .get(queryPreamble + "&siteId=" + NON_EXISTENT_SITE_ID)
    // .then()
    // .assertThat()
    // .body("size()", equalTo(0));
    // }
}
