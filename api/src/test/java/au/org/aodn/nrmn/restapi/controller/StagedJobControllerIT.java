package au.org.aodn.nrmn.restapi.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.dto.stage.UploadResponse;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import au.org.aodn.nrmn.restapi.service.S3ClientProvider;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class StagedJobControllerIT {

    @Container
    static public LocalStackContainer localstack = new LocalStackContainer()
            .withServices(S3);

    public static S3Client client;

    @MockBean
    private S3ClientProvider provider;

    @Autowired
    JwtTokenProvider jwtProvider;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Autowired
    public TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomServerPort;

    @BeforeAll
    static public void setup() {
        localstack.start();
        client = S3Client
                .builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey()
                )))
                .region(Region.of(localstack.getRegion()))
                .build();
        CreateBucketResponse bucketResp = client.createBucket(CreateBucketRequest.builder().bucket("nrmn-dev").build());
        assertTrue(bucketResp.sdkHttpResponse().isSuccessful());
    }

    @AfterAll
    static public void tearDown() {
        localstack.stop();
        client = null;

    }


    @Test
    @WithUserDetails("test@gmail.com")
    public void UploadingShortCorrectIngestFileShouldbeOK() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        val auth = getContext().getAuthentication();
        val token = jwtProvider.generateToken(auth);

        val reqUpload = new RequestWrapper<LinkedMultiValueMap<String, Object>, UploadResponse>();
        val file = new FileSystemResource("src/test/resources/sheets/correctShortHeader.xlsx");
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", file);
        parameters.add("withExtendedSizes", false);
        parameters.add("programId", 55);


        val resp = reqUpload
                .withContentType(MediaType.MULTIPART_FORM_DATA)
                .withToken(token)
                .withEntity(parameters)
                .withMethod(HttpMethod.POST)
                .withUri(_createUrl("/api/stage/upload"))
                .withResponseType(UploadResponse.class)
                .build(testRestTemplate);

        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertEquals(resp.getBody().getFile().get().getRowCount(), 28);
    }

    private String _createUrl(String uri) {
        return "http://localhost:" + randomServerPort + uri;
    }

    @Test
    @WithUserDetails("test@gmail.com")
    public void UploadingLongCorrectIngestFileShouldbeOK() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        val auth = getContext().getAuthentication();
        val token = jwtProvider.generateToken(auth);
        val reqUpload = new RequestWrapper<LinkedMultiValueMap<String, Object>, UploadResponse>();
        val file = new FileSystemResource("src/test/resources/sheets/correctLongHeader.xlsx");
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", file);
        parameters.add("withExtendedSizes", true);
        parameters.add("programId", 55);


        val resp = reqUpload
                .withContentType(MediaType.MULTIPART_FORM_DATA)
                .withEntity(parameters)
                .withToken(token)
                .withMethod(HttpMethod.POST)
                .withResponseType(UploadResponse.class)
                .withUri(_createUrl("/api/stage/upload"))
                .build(testRestTemplate);

        assertEquals(resp.getStatusCode(), HttpStatus.OK);
        assertEquals(resp.getBody().getFile().get().getRowCount(), 34);
    }
    
    @Test
    @WithUserDetails("test@gmail.com")
    public void emptyFileShouldFail() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        val auth = getContext().getAuthentication();
        val token = jwtProvider.generateToken(auth);
        val reqUpload = new RequestWrapper<LinkedMultiValueMap<String, Object>, UploadResponse>();
        val file = new FileSystemResource("src/test/resources/sheets/empty.xlsx");
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", file);
        parameters.add("withExtendedSizes", true);
        parameters.add("programId", 55);

        val resp = reqUpload
                .withContentType(MediaType.MULTIPART_FORM_DATA)
                .withEntity(parameters)
                .withToken(token)
                .withMethod(HttpMethod.POST)
                .withResponseType(UploadResponse.class)
                .withUri(_createUrl("/api/stage/upload"))
                .build(testRestTemplate);
        assertEquals(resp.getStatusCode().value(), 422);
        assertEquals(resp.getBody().getErrors().stream().findFirst().get().getMessage(), "Empty DATA sheet");
    }

}
