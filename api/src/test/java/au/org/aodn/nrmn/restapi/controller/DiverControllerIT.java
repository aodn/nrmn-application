package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.DiverTestData;
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
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class DiverControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private DiverTestData diverTestData;

    @Autowired
    private JwtToken jwtToken;

    RequestSpecification getDataSpec;

    @BeforeEach
    public void setup() {

        getDataSpec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetDiverNameList() {
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            diverTestData.persistedDiver(diverTestData.buildWith(i, String.valueOf(i)));
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"diver.initials\",\"ops\":\"contains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("divers")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(12, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(12, items.size());
        items.forEach(item -> {
            assertTrue(item.get("initials").toString().contains("12"));
        });

        // filter by surveyId not contains 12
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"diver.initials\",\"ops\":\"notContains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("divers")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(138, obj.get("lastRow"));  // Total 138

        items = obj.get("items");
        assertEquals(100, items.size());        // One page max 100
        items.forEach(item -> {
            assertFalse(item.get("initials").toString().contains("12"));
        });
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByDiverNameCompositeAndPage() {
        int totalRecord = 130;
        for (int i = 0; i < totalRecord; i++) {
            diverTestData.persistedDiver(diverTestData.buildWith(i, String.valueOf(i)));
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"diver.fullName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"contains\",\"val\":\"12\"},{\"ops\":\"contains\",\"val\":\"13\"}]}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("divers")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(14, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(14, items.size());
        items.forEach(item -> {
            assertTrue(item.get("fullName").toString().contains("12") || item.get("fullName").toString().contains("13") );
        });

        // filter by surveyId not contains 12 or 13 and go to page 1, basically this means everything and no need assert
        // individual items
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"diver.fullName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("divers")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(130, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(30, items.size());        // One page max 100, total 150 -100 equals 50

        // filter by surveyId not contains 12 and 13 and go to page 1, basically this means skip anything 12 and 13
        obj = given().spec(getDataSpec)
                .queryParam("filters", "[{\"field\":\"diver.fullName\",\"ops\":\"AND\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("divers")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(116, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(16, items.size());
        items.forEach(item -> {
            assertTrue(!item.get("fullName").toString().contains("12") && !item.get("fullName").toString().contains("13"));
        });
    }
}