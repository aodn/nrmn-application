package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;
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

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class AuthControllerIT {

    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Test
    public void signup() throws Exception {

        RequestWrapper<SignUpRequest, SecUser> reqBuilder = new RequestWrapper<SignUpRequest, SecUser>();
        val signupReq = new SignUpRequest("tj@gmail.com", "FirstName TestName", "#12Trois", Collections.emptyList());

        ResponseEntity<SecUser> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signup"))
                .withMethod(HttpMethod.POST)
                .withEntity(signupReq)
                .withResponseType(SecUser.class)
                .build(testRestTemplate);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody().getEmail(), "tj@gmail.com");

    }

    @Test
    public void loginLogout() throws Exception {
        val logReq = new LoginRequest("auth@gmail.com", "#12Trois");
        val reqBuilder = new RequestWrapper<LoginRequest, JwtAuthenticationResponse>();

        ResponseEntity<JwtAuthenticationResponse> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signin"))
                .withMethod(HttpMethod.POST)
                .withEntity(logReq)
                .withResponseType(JwtAuthenticationResponse.class)
                .build(testRestTemplate);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getAccessToken().length() > 10, true);
        assertEquals(response.getBody().getTokenType(), "Bearer");

        val token = response.getBody().getAccessToken();




        val logOutReq = new RequestWrapper<Void, Void>();
        val resp = logOutReq
                .withAppJson()
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withUri(_createUrl("/api/auth/signout"))
                .build(testRestTemplate);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void badSignin() throws Exception {
        val logReq = new LoginRequest("", "#12Trois");
        val reqBuilder = new RequestWrapper<LoginRequest, JwtAuthenticationResponse>();

        ResponseEntity<JwtAuthenticationResponse> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signin"))
                .withMethod(HttpMethod.POST)
                .withEntity(logReq)
                .withResponseType(JwtAuthenticationResponse.class)
                .build(testRestTemplate);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void badSignup() throws Exception {
        RequestWrapper<SignUpRequest, SecUser> reqBuilder = new RequestWrapper<SignUpRequest, SecUser>();
        val signupReq = new SignUpRequest("test@gmail.com", "F", "#12Trois", Collections.emptyList());

        ResponseEntity<SecUser> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signup"))
                .withMethod(HttpMethod.POST)
                .withEntity(signupReq)
                .withResponseType(SecUser.class)
                .build(testRestTemplate);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
