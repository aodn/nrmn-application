package au.org.aodn.nrmn.restapi.service;


import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
public class SpreadSheetServiceIT {

    @Autowired
    SpreadSheetService sheetService;

    @MockBean
    private S3ClientProvider provider;


    public static S3Client client;

    @Container
    static public LocalStackContainer localstack = new LocalStackContainer()
            .withServices(S3);

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
        client.createBucket(CreateBucketRequest.builder().bucket("bucket").build());

    }
    @AfterAll
    static public  void tearDown(){
        localstack.stop();
        client = null;

    }

    @Test
    public void correctShortheaderShouldBeValid() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        val rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");

        val validSheet =
                sheetService.validatedExcelFile(
                        "idfileShortValid-1234",
                        new MockMultipartFile("sheets/correctShortHeader.xsx", rottnestInput),
                        false
                );
        assertTrue(validSheet.isValid());
    }

    @Test
    public void correctLongheaderShouldBeValid() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctLongHeader.xlsx");
        val validSheet =
                sheetService.validatedExcelFile(
                        "idfileLongValid-1234",
                        new MockMultipartFile("sheets/correctLongHeader.xsx", rottnestInput),
                        true);
        assertTrue(validSheet.isValid());
    }

    @Test
    public void missingDataSheethouldBeInvalid() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingDataSheet.xlsx");
        val validSheet =
                sheetService.validatedExcelFile(
                        "idfileMissingInvalid-123",
                        new MockMultipartFile("sheets/missingDataSheet.xsx", rottnestInput),
                        false);
        assertTrue(validSheet.isInvalid());
    }

    @Test
    public void missingColunmsSheethouldBeInvalid() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);

        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingColumnsHeader.xlsx");
        val validSheet = sheetService.validatedExcelFile(
                "idfileInvalid-1245",
                new MockMultipartFile("sheets/missingColumnsHeader.xsx", rottnestInput),
                false);
        assertTrue(validSheet.isInvalid());
    }


    @Test
    void validFileShouldBeCorrectlyTransformToStageSurvey() throws Exception {
        Mockito.when(provider.getClient()).thenReturn(client);
        val file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
        val sheetWithHeader = sheetService.validatedExcelFile(
                "testFile-1234561",
                new MockMultipartFile("sheets/correctShortHeader3.xsx", file3.getInputStream()),
                false).orElseGet(() -> null);
        val stageSurveys = sheetService.sheets2Staged(sheetWithHeader);
        assertEquals(stageSurveys.size(), 2);
        val obs1 = stageSurveys.get(0);

        //test Double
        assertEquals(obs1.getLatitude(), "-41.253706");
        assertEquals(obs1.getLongitude(), "148.339749");
        //test Map filling
        assertEquals(obs1.getMeasureJson().size(), 4);
        assertEquals(obs1.getMeasureJson().get("162.5"), "4");
        // test Macro
        assertEquals(obs1.getSpecies(), "Caesioperca rasor");
    }
}

