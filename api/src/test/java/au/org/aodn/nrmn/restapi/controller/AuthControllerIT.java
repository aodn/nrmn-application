package au.org.aodn.nrmn.restapi.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.val;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class AuthControllerIT {

    private static final String NON_EXISTENT_DIVER = "923579";
    private static final String INVALID_TOKEN = "";
    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Test
    public void loginLogout() throws Exception {
        ResponseEntity<JwtAuthenticationResponse> response = loginResponse("auth@gmail.com", "#12Trois");

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getAccessToken().length() > 10, true);
        assertEquals(response.getBody().getTokenType(), "Bearer");

        val token = response.getBody().getAccessToken();

        ResponseEntity<Void> resp = logoutResponse(token);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testLoginLogoutTokenAuth() throws Exception {
        assertGetMissingDiverReturns(INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        String firstLoginToken= login("auth@gmail.com", "#12Trois");
        assertGetMissingDiverReturns(firstLoginToken, HttpStatus.NOT_FOUND);
        logoutResponse(firstLoginToken);
        assertGetMissingDiverReturns(firstLoginToken, HttpStatus.UNAUTHORIZED);
        String secondLoginToken = login("auth@gmail.com", "#12Trois");
        assertGetMissingDiverReturns(secondLoginToken, HttpStatus.NOT_FOUND);
    }

    private void assertGetMissingDiverReturns(String token, HttpStatus statusCode) {
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(_createUrl(""))
                .setBasePath("/api/divers")
                .setContentType("application/json")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();

        given().spec(spec)
               .auth()
               .oauth2(token)
               .get(NON_EXISTENT_DIVER)
               .then()
               .assertThat()
               .statusCode(statusCode.value());
    }

    @Test
    public void badSignin() throws Exception {
        ResponseEntity<JwtAuthenticationResponse> response = loginResponse("", "#12Trois");

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

    private ResponseEntity<JwtAuthenticationResponse> loginResponse(String username, String password) throws Exception {
        val logReq = new LoginRequest(username, password);
        val reqBuilder = new RequestWrapper<LoginRequest, JwtAuthenticationResponse>();

        return reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signin"))
                .withMethod(HttpMethod.POST)
                .withEntity(logReq)
                .withResponseType(JwtAuthenticationResponse.class)
                .build(testRestTemplate);
    }

    private ResponseEntity<Void> logoutResponse(String token) throws Exception {
        val logOutReq = new RequestWrapper<Void, Void>();
        return logOutReq
                .withAppJson()
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withUri(_createUrl("/api/auth/signout"))
                .build(testRestTemplate);
    }

    private String login(String username, String password) throws Exception {
        val response = loginResponse(username, password);
        return response.getBody().getAccessToken();
    }

}
