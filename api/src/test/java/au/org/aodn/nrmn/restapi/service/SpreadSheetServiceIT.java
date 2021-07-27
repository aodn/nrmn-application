package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.service.SurveyContentsHandler.ParsedSheet;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import lombok.val;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class SpreadSheetServiceIT {

    @Autowired
    SpreadSheetService sheetService;

    @Test
    public void correctShortheaderShouldBeValid() throws Exception {
        val rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");

        val validSheet = sheetService.stageXlsxFile(
                new MockMultipartFile("sheets/correctShortHeader.xlsx", rottnestInput), false);
        assertTrue(validSheet != null);
    }

    @Test
    public void correctLongheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/correctLongHeader.xlsx");
        val validSheet = sheetService.stageXlsxFile(
                new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), true);
        assertTrue(validSheet != null);
    }

    @Test
    public void correctLongheaderIngestedAsShortShouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/correctLongHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        // Reject the sheet for having unexpected headers
        assertTrue(error != null);
    }

    @Test
    public void missingDataSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingDataSheet.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/missingDataSheet.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(error != null);
    }

    @Test
    public void missingColumnsSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingColumnsHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/missingColumnsHeader.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(error != null);
    }

    @Test
    public void mismatchedColumnsSheethouldBeInvalid() throws Exception {

        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/mismatchedColumnsHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/mismatchedColumnsHeader.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(error != null);
    }

    @Test
    void validFileShouldBeCorrectlyTransformToStageSurvey() throws Exception {
        val file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
        ParsedSheet parsedSheet = sheetService.stageXlsxFile(new MockMultipartFile("sheets/correctShortHeader3.xlsx", file3.getInputStream()), false);

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
        val file = new FileSystemResource("src/test/resources/sheets/dateFormats.xlsx");
        val mockFile = new MockMultipartFile("sheets/dateFormats.xlsx", file.getInputStream());
        val stageSurveys = sheetService.stageXlsxFile(mockFile, false).getStagedRows();

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
