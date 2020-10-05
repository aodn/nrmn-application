package au.org.aodn.nrmn.restapi.it;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.dto.auth.SignUpRequest;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DataJpaTest
@ActiveProfiles("cicd")
public class AuthenticationIT {

    @Autowired
    public TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    @Test
    @Sql( {"/testdata/FILL_ROLES.sql", "/testData/FILL_USER.sql"})
    public void signup() {try {
        URI uri = new URI(_createUrl("/api/auth/signup"));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "Application/json");
        val signupReq = new SignUpRequest("test@hello.com", "FirstName TestName", "#12Trois", Collections.emptyList());
        HttpEntity<SignUpRequest> request = new HttpEntity<>(signupReq, headers);

        ResponseEntity<SecUserEntity> response = testRestTemplate.postForEntity(
                uri,
                request,
                SecUserEntity.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getEmail(), "test@hello.com");
    } catch (Exception e) {
        assert(false);
    }
    }

    private String _createUrl (String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

}
