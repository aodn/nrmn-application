package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.db.model.Location;
import au.org.aodn.nrmn.db.model.Site;
import au.org.aodn.nrmn.restapi.model.db.LocationTestData;
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
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class SiteControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private SiteTestData siteTestData;

    @Autowired
    private LocationTestData locationTestData;

    @Autowired
    private JwtToken jwtToken;

    private RequestSpecification spec;

    @BeforeEach
    public void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(String.format("http://localhost:%s", port))
                .setBasePath("/api/v1")
                .setContentType(ContentType.JSON)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testPostSite() {
        Site testSite = siteTestData.persistedSite();

        given()
                .spec(spec)
                .auth()
                .oauth2(jwtToken.get())
                .get("sites")
                .then()
                .assertThat()
                .statusCode(200)
                .body("items.siteId", hasItems(testSite.getSiteId()));
    }

    @Test
    @WithUserDetails("test@example.com")
    public void testGetLocationNameList() {

        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            Location loc = locationTestData.buildWith(i);
            Site site = siteTestData.buildWith(i);

            site.setLocation(loc);
            siteTestData.persistedSite(site);
        }

        // filter by surveyId contains 12
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"site.locationName\",\"ops\":\"contains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("sites")
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
                .queryParam("filters", "[{\"field\":\"site.locationName\",\"ops\":\"notContains\",\"val\":\"12\"}]")
                .queryParam("page", 0)
                .auth()
                .oauth2(jwtToken.get())
                .get("sites")
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

    @Test
    @WithUserDetails("test@example.com")
    public void testFilterByLocationNameCompositeAndPage() {
        int totalRecord = 150;
        for (int i = 0; i < totalRecord; i++) {
            Location loc = locationTestData.buildWith(i);
            Site site = siteTestData.buildWith(i);

            site.setLocation(loc);
            siteTestData.persistedSite(site);
        }

        // filter by surveyId not contains 12 or 13 and go to page 1, basically this means everything and no need assert
        // individual items
        Map<String, ArrayList<Map<String, Object>>> obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"location.locationName\",\"ops\":\"OR\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("sites")
                .then()
                .assertThat()
                .statusCode(200).extract().jsonPath().get("");

        assertEquals(150, obj.get("lastRow"));

        List<Map<String, Object>> items = obj.get("items");
        assertEquals(50, items.size());        // One page max 100, total 150 -100 equals 50

        // filter by surveyId not contains 12 and 13 and go to page 1, basically this means skip anything 12 and 13
        obj = given().spec(spec)
                .queryParam("filters", "[{\"field\":\"site.locationName\",\"ops\":\"AND\",\"conditions\":[{\"ops\":\"notContains\",\"val\":\"12\"},{\"ops\":\"notContains\",\"val\":\"13\"}]}]")
                .queryParam("page", 1)
                .auth()
                .oauth2(jwtToken.get())
                .get("sites")
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
}