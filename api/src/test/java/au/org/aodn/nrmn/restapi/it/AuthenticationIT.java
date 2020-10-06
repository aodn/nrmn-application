package au.org.aodn.nrmn.restapi.it;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import au.org.aodn.nrmn.restapi.it.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.model.db.SurveyEntity;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cicd")
public class AuthenticationIT {

    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Test
    @Sql({"/testdata/FILL_ROLES.sql", "/testdata/FILL_USER.sql", "/testdata/FILL_FOUR_SURVEY.sql"})
    public void signup() {
        try {

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
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    public void loginLogout() {
        try {
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
                    .withToken(token)
                    .withUri(_createUrl("/api/auth/signout"))
                    .build(testRestTemplate);
            assertEquals(resp.getStatusCode(), HttpStatus.OK);

            val secondSurVeyReq = surveyReadyReq.build(testRestTemplate);
            assertEquals(secondSurVeyReq.getStatusCode(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            assertFalse(false, "Exception Found");
        }
    }

    @Test
    public void badSignin() {
        try {
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
        }catch (Exception e) {
            assertFalse(false, "Exception Found in badSignin");
        }
    }

    @Test
    public void badSignup() {
        try {
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
        } catch (Exception e) {
            assertFalse(false, "Exception Found in badSignup");

        }
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
