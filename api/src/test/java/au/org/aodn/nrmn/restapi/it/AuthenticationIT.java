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

            RequestWrapper<SignUpRequest, SecUserEntity> reqBuilder =  new RequestWrapper<SignUpRequest, SecUserEntity>();
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
    public void login() {
        try {
            val logReq = new LoginRequest("test@hello.com","#12Trois");
            val reqBuilder =  new RequestWrapper<LoginRequest, JwtAuthenticationResponse>();

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

        } catch (Exception e) {
            assertFalse(false, "Exception Found");
        }
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
