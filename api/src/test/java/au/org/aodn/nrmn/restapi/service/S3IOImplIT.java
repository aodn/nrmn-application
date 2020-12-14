package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@SpringBootTest
@WithTestData
class S3IOImplIT {

    public static S3Client client;

    @Autowired
    S3IO s3provider;

    @Container
    static public LocalStackContainer localstack = new LocalStackContainer()
            .withServices(S3);


    @MockBean
    S3ClientProvider clientProvider;

    @BeforeAll
    static public  void setup(){
        localstack.start();
        client = S3Client
                .builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey()
                )))
                .region(Region.of(localstack.getRegion()))
                .build();
        client.createBucket(CreateBucketRequest.builder().bucket("nrmn-dev").build());
    }

    @AfterAll
    static public  void tearDown(){
        localstack.stop();
        client = null;
    }

    @Test
    public void writeFileShouldBeOK() throws Exception {
        Mockito.when(clientProvider.getClient()).thenReturn(client);
        val rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");
       val res = s3provider.write("/raw-survey/correctShortHeader-1234", new MockMultipartFile("sheets/correctShortHeader.xlsx", rottnestInput));
      assertTrue(res.isSuccess());
        val checkfile =client
                .getObject(
                        GetObjectRequest
                                .builder()
                                .bucket("nrmn-dev")
                                .key("/raw-survey/correctShortHeader-1234").build())
                .response();
        assertTrue(checkfile.sdkHttpResponse().isSuccessful());
    }
}


