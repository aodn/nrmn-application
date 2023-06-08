package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import au.org.aodn.nrmn.restapi.test.JwtToken;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class IngestionControllerIT {

    protected ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    protected Logger logger = LoggerFactory.getLogger(IngestionControllerIT.class);

    @LocalServerPort
    private int port;

    @Autowired
    public TestRestTemplate testRestTemplate;

    @Autowired
    JwtTokenProvider jwtProvider;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    StagedJobRepository stagedJobRepository;

    @Autowired
    ObservationRepository observationRepository;

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

    /**
     * Post the job and then use api to check if the job changed from status INGESTING to INGESTED
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void ingestWorksForValidatedJob() throws Exception {

        RequestWrapper<String, Map> reqBuilder = new RequestWrapper<>();

        Authentication auth = getContext().getAuthentication();
        String token = jwtProvider.generateToken(auth);

        long initialObservationCount = observationRepository.count();

        ResponseEntity<Map> response = reqBuilder
                .withUri(_createUrl("/api/v1/ingestion/ingest/109"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        // Workflow changed from sync call, to post job and check job status
        assertEquals(HttpStatus.OK, response.getStatusCode());

        final String id = response.getBody().get("jobLogId").toString();
        final CountDownLatch latch = new CountDownLatch(1);

        // Separate thread to avoid blocking the injection
        executorService.execute(() -> {
            try {
                boolean isIngested = false;
                for (int i = 0; i < 10; i++) {
                    ResponseEntity<Map> res = reqBuilder
                            .withUri(_createUrl("/api/v1/ingestion/ingest/" + id))
                            .withMethod(HttpMethod.GET)
                            .withToken(token)
                            .withResponseType(Map.class)
                            .build(testRestTemplate);

                    if("INGESTED".equals(res.getBody().get("jobStatus"))) {
                        isIngested = true;
                        break;
                    }
                    else {
                        latch.await(2, TimeUnit.SECONDS);
                    }
                }
                assertTrue("Job status changed to INGESTED", isIngested);
            }
            catch(Exception e) {
                logger.error("Exception when create reqBuilder");
            }
            finally {
                latch.countDown();
            }
        });

        latch.await();
        assertEquals(initialObservationCount + 1, observationRepository.count());
    }
    /**
     * Job ingest fail if you have not validated the job before ingest.
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void ingestFailsForUnvalidatedJob() throws Exception {
        // Bad request will result in text instead of json
        RequestWrapper<String, Map> reqBuilder = new RequestWrapper<>();

        Authentication auth = getContext().getAuthentication();
        String token = jwtProvider.generateToken(auth);

        long initialObservationCount = observationRepository.count();

        ResponseEntity<Map> response = reqBuilder
                .withUri(_createUrl("/api/v1/ingestion/ingest/120"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        StagedJob job = stagedJobRepository.findById(120L).get();
        assertEquals(StatusJobType.PENDING, job.getStatus());

        assertEquals(initialObservationCount, observationRepository.count());
    }
    /**
     * Job ingest fail if job id not found
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void ingestFailsForUnknownJob() throws Exception {
        // Bad request will result in text instead of json
        RequestWrapper<String, Map> reqBuilder = new RequestWrapper<>();

        Authentication auth = getContext().getAuthentication();
        String token = jwtProvider.generateToken(auth);

        long initialObservationCount = observationRepository.count();

        ResponseEntity<Map> response = reqBuilder
                .withUri(_createUrl("/api/v1/ingestion/ingest/111111111"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(response.getBody().get("jobStatus"), StatusJobType.FAILED.toString());
        assertEquals(response.getBody().get("message"), "Job with given id does not exist. jobId: 111111111");
    }
    /**
     * Job ingest fail due to invalid job
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void ingestFailsForInvalidJob() throws Exception {
        // Bad request will result in text instead of json
        RequestWrapper<String, Map> reqBuilder = new RequestWrapper<>();

        Authentication auth = getContext().getAuthentication();
        String token = jwtProvider.generateToken(auth);

        ResponseEntity<Map> response = reqBuilder
                .withUri(_createUrl("/api/v1/ingestion/ingest/110"))
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        final String id = response.getBody().get("jobLogId").toString();
        final CountDownLatch latch = new CountDownLatch(1);

        // Separate thread to avoid blocking the injection
        executorService.execute(() -> {
            try {
                boolean isError = false;
                String errorMessage = "";
                for (int i = 0; i < 10; i++) {
                    ResponseEntity<Map> res = reqBuilder
                            .withUri(_createUrl("/api/v1/ingestion/ingest/" + id))
                            .withMethod(HttpMethod.GET)
                            .withToken(token)
                            .withResponseType(Map.class)
                            .build(testRestTemplate);

                    if("ERROR".equals(res.getBody().get("jobStatus"))) {
                        isError = true;
                        errorMessage = res.getBody().get("message").toString();
                        break;
                    }
                    else {
                        latch.await(2, TimeUnit.SECONDS);
                    }
                }
                assertTrue("Job status changed to ERROR", isError);
                assertTrue(errorMessage.startsWith("Site value seems invalid for program name"));
            }
            catch(Exception e) {
                logger.error("Exception when create reqBuilder");
            }
            finally {
                latch.countDown();
            }
        });

        latch.await();
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + port + uri;
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
                .post("ingest/123")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
