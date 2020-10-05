package au.org.aodn.nrmn.restapi.it;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.dto.auth.LoginRequest;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
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

import java.net.URI;
import java.util.Collections;

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
            val uri = new URI(_createUrl("/api/auth/signup"));
            val headers = new HttpHeaders();
            headers.set("Content-type", "Application/json");
            val signupReq = new SignUpRequest("test@hello.com", "FirstName TestName", "#12Trois", Collections.emptyList());
            val httpSignupReq= new HttpEntity<>(signupReq, headers);

            ResponseEntity<SecUserEntity> response = testRestTemplate.postForEntity(
                    uri,
                    httpSignupReq,
                    SecUserEntity.class);

            assertEquals(response.getStatusCode(), HttpStatus.CREATED);
            assertEquals(response.getBody().getEmail(), "test@hello.com");
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    public void login() {
        try {
            val headers = new HttpHeaders();
            headers.set("Content-type", "Application/json");
            val signupReq = new SignUpRequest("test@hello.com", "FirstName TestName", "#12Trois", Collections.emptyList());

            val uriLogin = new URI(_createUrl("/api/auth/signin"));
            val logReq = new LoginRequest(signupReq.getEmail(), signupReq.getPassword());
            val httpLoginReq = new HttpEntity<>(logReq, headers);

            ResponseEntity<JwtAuthenticationResponse> loginResp = testRestTemplate.postForEntity(
                    uriLogin,
                    httpLoginReq,
                    JwtAuthenticationResponse.class);

            assertEquals(loginResp.getStatusCode(), HttpStatus.OK);
            assertEquals(loginResp.getBody().getAccessToken().length() > 10, true);
            assertEquals(loginResp.getBody().getTokenType(), "Bearer");

        } catch (Exception e) {
            assertFalse(false, "Exception Found");
        }
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
