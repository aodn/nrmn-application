package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class IngestionControllerIT {
    @Autowired
    public TestRestTemplate testRestTemplate;
    @Autowired
    JwtTokenProvider jwtProvider;
    @LocalServerPort
    int randomServerPort;
    @Autowired
    StagedJobRepository stagedJobRepository;
    @Autowired
    ObservationRepository observationRepository;

    @Test
    @WithUserDetails("test@gmail.com")
    public void ingestWorksForValidatedJob() throws Exception {

        RequestWrapper<String, SecUser> reqBuilder = new RequestWrapper<String, SecUser>();

        val auth = getContext().getAuthentication();
        val token = jwtProvider.generateToken(auth);

        long initialObservationCount = observationRepository.count();

        ResponseEntity<SecUser> response = reqBuilder
                .withUri(_createUrl("/api/ingestion/ingest/109"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        StagedJob job = stagedJobRepository.findById(109L).get();
        assertEquals(StatusJobType.INGESTED, job.getStatus());

        assertEquals(initialObservationCount + 1, observationRepository.count());
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void ingestFailsForUnvalidatedJob() throws Exception {

        RequestWrapper<String, SecUser> reqBuilder = new RequestWrapper<String, SecUser>();

        val auth = getContext().getAuthentication();
        val token = jwtProvider.generateToken(auth);

        long initialObservationCount = observationRepository.count();

        ResponseEntity<SecUser> response = reqBuilder
                .withUri(_createUrl("/api/ingestion/ingest/120"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        StagedJob job = stagedJobRepository.findById(120L).get();
        assertEquals(StatusJobType.STAGED, job.getStatus());

        assertEquals(initialObservationCount, observationRepository.count());
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }
}
