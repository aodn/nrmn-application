package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.model.db.SurveyEntity;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cicd")
public class AuthControllerIT {

    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Test
    @Sql({"/testdata/FILL_ROLES.sql", "/testdata/FILL_USER.sql", "/testdata/FILL_FOUR_SURVEY.sql"})
    public void signup() throws Exception {

        RequestWrapper<SignUpRequest, SecUserEntity> reqBuilder = new RequestWrapper<SignUpRequest, SecUserEntity>();
        val signupReq = new SignUpRequest("test@hello.com", "FirstName TestName", "#12Trois", Collections.emptyList());

        ResponseEntity<SecUserEntity> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signup"))
                .withMethod(HttpMethod.POST)
                .withEntity(signupReq)
                .withResponseType(SecUserEntity.class)
                .build(testRestTemplate);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody().getEmail(), "test@hello.com");

    }

    @Test
    public void loginLogout() throws Exception {
        val logReq = new LoginRequest("test@hello.com", "#12Trois");
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
        val reqBuilderSurvey = new RequestWrapper<Void, List<SurveyEntity>>();
        val surveyReadyReq = reqBuilderSurvey
                .withAppJson()
                .withResponseType((Class<List<SurveyEntity>>) (Class<?>) List.class)
                .withToken(token)
                .withUri(_createUrl("/api/survey"))
                .withMethod(HttpMethod.GET);

        val surveyResp = surveyReadyReq.build(testRestTemplate);

        assertEquals(surveyResp.getBody().size(), 4);

        val logOutReq = new RequestWrapper<Void, Void>();
        val resp = logOutReq
                .withAppJson()
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withUri(_createUrl("/api/auth/signout"))
                .build(testRestTemplate);
        assertEquals(resp.getStatusCode(), HttpStatus.OK);

        val reqSignupAgain = new RequestWrapper<SignUpRequest, Void>();
        val respSignupAgain = reqSignupAgain
                .withMethod(HttpMethod.POST)
                .withAppJson()
                .withToken(token)
                .withUri(_createUrl("/api/auth/signup"))
                .build(testRestTemplate);

        assertEquals(respSignupAgain.getStatusCode(), HttpStatus.BAD_REQUEST);

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
        RequestWrapper<SignUpRequest, SecUserEntity> reqBuilder = new RequestWrapper<SignUpRequest, SecUserEntity>();
        val signupReq = new SignUpRequest("test_hello.com", "F", "#12Trois", Collections.emptyList());

        ResponseEntity<SecUserEntity> response = reqBuilder
                .withAppJson()
                .withUri(_createUrl("/api/auth/signup"))
                .withMethod(HttpMethod.POST)
                .withEntity(signupReq)
                .withResponseType(SecUserEntity.class)
                .build(testRestTemplate);

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
