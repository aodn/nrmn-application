package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.SurveyTestData;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.val;

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
    private String NON_EXISTENT_SURVEY = "123123123";

    @Autowired
    private JwtToken jwtToken;

    RequestSpecification allSurveysSpec, getSurveySpec;

    @BeforeEach
    public void setup() {
        allSurveysSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/data/surveys")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();

        getSurveySpec = new RequestSpecBuilder()
            .setBaseUri(String.format("http://localhost:%s", port))
            .setBasePath("/api/data/survey/")
            .setContentType("application/json")
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new RequestLoggingFilter())
            .build();
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testGetSurveyList() {

        val testSurvey = surveyTestData.persistedSurvey();

        given()
                .spec(allSurveysSpec)
                .auth()
                .oauth2(jwtToken.get())
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .body("surveyId", hasItems(testSurvey.getSurveyId()));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testGetSurvey() {

        val testSurvey = surveyTestData.persistedSurvey();

        given().spec(getSurveySpec)
               .auth()
               .oauth2(jwtToken.get())
               .get(testSurvey.getSurveyId().toString())
               .then()
               .assertThat()
               .statusCode(200)
               .body("surveyId", equalTo(testSurvey.getSurveyId()));
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void testNoSurvey() {

        val testSurvey = surveyTestData.persistedSurvey();

        assertFalse(testSurvey.getSurveyId().toString().equals(NON_EXISTENT_SURVEY));

        given().spec(getSurveySpec)
               .auth()
               .oauth2(jwtToken.get())
               .get(NON_EXISTENT_SURVEY)
               .then()
               .assertThat()
               .statusCode(404);
    }
}
