package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.*;
import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
import au.org.aodn.nrmn.restapi.model.db.MeowRegionTestData;
import au.org.aodn.nrmn.restapi.model.db.SiteTestData;
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
public class LocationControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private MeowRegionTestData meowRegionTestData;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private JwtToken jwtToken;

    RequestSpecification spec;

    @BeforeEach
    public void setup() {

        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1/")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetLocationNameList() {

        int totalRecord = 150;
        for(int i = 0; i < totalRecord; i++) {
            locationTestData.persistedLocation(locationTestData.buildWith(i));
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"contains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(12, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(12, items.size());
        items.forEach(item -> {
            assertTrue(item.get("locationName").toString().contains("12"));
        });

        // filter by surveyId not contains 12
        obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"notContains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(138, obj.get("lastRow"));  // Total 138

        items = obj.get("items");
        assertEquals(100, items.size());        // One page max 100
        items.forEach(item -> {
            assertFalse(item.get("locationName").toString().contains("12"));
        });
    }

    /**
     * This test diff from the testFilterBySurveyId() by applying two filters to the same field.
     */
    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByLocationNameCompositeAndPage() {
        int totalRecord = 150;
        for(int i = 0; i < totalRecord; i++) {
            locationTestData.persistedLocation(locationTestData.buildWith(i));
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"contains\",\"val\":\"12\"},{\"ops\":\"contains\",\"val\":\"13\"}]}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(24, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(24, items.size());
        items.forEach(item -> {
            assertTrue(item.get("locationName").toString().contains("12") || item.get("locationName").toString().contains("13") );
        });

        // filter by surveyId not contains 12 or 13 and go to page 1, basically this means everything and no need assert
        // individual items
        obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(150, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(50, items.size());        // One page max 100, total 150 -100 equals 50

        // filter by surveyId not contains 12 and 13 and go to page 1, basically this means skip anything 12 and 13
        obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"AND\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(126, obj.get("lastRow"));

        items = obj.get("items");
        assertEquals(26, items.size());
        items.forEach(item -> {
            assertTrue(!item.get("locationName").toString().contains("12") && !item.get("locationName").toString().contains("13"));
        });
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByEcoRegions() {

        // These test pack should cover the area of site below.
        MeowEcoRegions meow0 = meowRegionTestData.buildWith(0);
        MeowEcoRegions meow1 = meowRegionTestData.buildWith(1);
        MeowEcoRegions meow2 = meowRegionTestData.buildWith(2);
        MeowEcoRegions meow3 = meowRegionTestData.buildWith(3);
        MeowEcoRegions meow4 = meowRegionTestData.buildWith(4);

        meowRegionTestData.persistedMeowRegion(meow0);
        meowRegionTestData.persistedMeowRegion(meow1);
        meowRegionTestData.persistedMeowRegion(meow2);
        meowRegionTestData.persistedMeowRegion(meow3);
        meowRegionTestData.persistedMeowRegion(meow4);

        int totalRecord = 30;
        for(int i = 0; i < totalRecord; i++) {

            Site site = siteTestData.buildWith(i, 0.1 + (i * 0.2), -0.1);
            Location location = locationTestData.buildWith(i);
            site.setLocation(location);

            site = siteTestData.persistedSite(site);
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.ecoRegions\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"contains\",\"val\":\"1\"},{\"ops\":\"contains\",\"val\":\"3\"}]}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(20, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(20, items.size());
        items.forEach(item -> {
            assertTrue(item.get("ecoRegions").toString().contains("1") || item.get("ecoRegions").toString().contains("3") );
        });

        // filter by surveyId contains 12
        obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.ecoRegions\",\"ops\":\"endsWith\",\"val\":\"1\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("locations")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(10, obj.get("lastRow"));
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
                .post("location")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .body("")           // Content isn't important as permission blocked before parsing body
                .put("location/123")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
