package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.InputStream;

import au.org.aodn.nrmn.restapi.service.SurveyContentsHandler.ParsedSheet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class SpreadSheetServiceIT {

        @Autowired
        SpreadSheetService sheetService;

        @MockBean
        private S3ClientProvider provider;

        public static S3Client client;

        @Container
        static public LocalStackContainer localstack = new LocalStackContainer().withServices(S3);

        @BeforeAll
        static public void setup() {
                localstack.start();
                client = S3Client.builder()
                                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
                                                .create(localstack.getAccessKey(), localstack.getSecretKey())))
                                .region(Region.of(localstack.getRegion())).build();
                client.createBucket(CreateBucketRequest.builder().bucket("nrmn-dev").build());

        }

        @AfterAll
        static public void tearDown() {
                localstack.stop();
                client = null;

        }

        @Test
        public void correctShortheaderShouldBeValid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                val rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");

                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/correctShortHeader.xlsx", rottnestInput), false);
                assertTrue(validSheet.isValid());
        }

        @Test
        public void correctLongheaderShouldBeValid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                InputStream rottnestInput = getClass().getClassLoader()
                                .getResourceAsStream("sheets/correctLongHeader.xlsx");
                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), true);
                assertTrue(validSheet.isValid());
        }

        @Test
        public void correctLongheaderIngestedAsShortShouldBeInvalid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                InputStream rottnestInput = getClass().getClassLoader()
                                .getResourceAsStream("sheets/correctLongHeader.xlsx");
                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), false);

                // Reject the sheet for having unexpected headers
                assertTrue(validSheet.isInvalid());
        }

        @Test
        public void missingDataSheethouldBeInvalid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                InputStream rottnestInput = getClass().getClassLoader()
                                .getResourceAsStream("sheets/missingDataSheet.xlsx");
                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/missingDataSheet.xlsx", rottnestInput), false);
                assertTrue(validSheet.isInvalid());
        }

        @Test
        public void missingColumnsSheethouldBeInvalid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);

                InputStream rottnestInput = getClass().getClassLoader()
                                .getResourceAsStream("sheets/missingColumnsHeader.xlsx");
                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/missingColumnsHeader.xlsx", rottnestInput), false);
                assertTrue(validSheet.isInvalid());
        }

        @Test
        public void mismatchedColumnsSheethouldBeInvalid() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);

                InputStream rottnestInput = getClass().getClassLoader()
                                .getResourceAsStream("sheets/mismatchedColumnsHeader.xlsx");
                val validSheet = sheetService.stageXlsxFile(
                                new MockMultipartFile("sheets/mismatchedColumnsHeader.xlsx", rottnestInput), false);
                assertTrue(validSheet.isInvalid());
        }

        @Test
        void validFileShouldBeCorrectlyTransformToStageSurvey() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                val file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
                ParsedSheet parsedSheet = sheetService.stageXlsxFile(
                        new MockMultipartFile("sheets/correctShortHeader3.xlsx", file3.getInputStream()), false)
                                                      .orElseGet(() -> null);
                val stageSurveys = parsedSheet.getStagedRows();
                assertEquals(stageSurveys.size(), 2);
                val obs1 = stageSurveys.get(0);

                // Test Double
                assertEquals(obs1.getLatitude(), "-41.253706");
                assertEquals(obs1.getLongitude(), "148.339749");

                // Test Map filling
                assertEquals(obs1.getMeasureJson().size(), 4);
                assertEquals(obs1.getMeasureJson().get(21), "4");

                // Test Macro
                assertEquals(obs1.getSpecies(), "Caesioperca rasor");
        }

        @Test
        void datesShouldNeverBeFormattedMDY() throws Exception {
                Mockito.when(provider.getClient()).thenReturn(client);
                val file = new FileSystemResource("src/test/resources/sheets/dateFormats.xlsx");
                val mockFile = new MockMultipartFile("sheets/dateFormats.xlsx", file.getInputStream());
                val stageSurveys = sheetService.stageXlsxFile(mockFile, false).orElseGet(() -> null).getStagedRows();

                // Test that dates are formatted the same way as they appear in the sheet.
                assertEquals("12/12/2019", stageSurveys.get(0).getDate());
                assertEquals("12/12/2019", stageSurveys.get(1).getDate());
                assertEquals("12/12/2019", stageSurveys.get(2).getDate());
                assertEquals("14/3/2019", stageSurveys.get(3).getDate());
                assertEquals("14/3/2019", stageSurveys.get(4).getDate());
                assertEquals("14/03/2019", stageSurveys.get(5).getDate());
                assertEquals("14/03/2019", stageSurveys.get(6).getDate());
        }
}
