package au.org.aodn.nrmn.restapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.service.SurveyContentsHandler.ParsedSheet;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class SpreadSheetServiceIT {

    @Autowired
    SpreadSheetService sheetService;

    @Test
    public void correctShortheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");

        ParsedSheet validSheet = sheetService.stageXlsxFile(
                new MockMultipartFile("sheets/correctShortHeader.xlsx", rottnestInput), false);
        assertTrue(validSheet != null);
    }

    @Test
    public void correctLongheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/correctLongHeader.xlsx");
                ParsedSheet validSheet = sheetService.stageXlsxFile(
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
        FileSystemResource file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
        ParsedSheet parsedSheet = sheetService.stageXlsxFile(new MockMultipartFile("sheets/correctShortHeader3.xlsx", file3.getInputStream()), false);

        List<StagedRow> stageSurveys = parsedSheet.getStagedRows();
        assertEquals(23, stageSurveys.size());
        StagedRow obs1 = stageSurveys.get(0);

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
        FileSystemResource file = new FileSystemResource("src/test/resources/sheets/dateFormats.xlsx");
        MockMultipartFile mockFile = new MockMultipartFile("sheets/dateFormats.xlsx", file.getInputStream());
        List<StagedRow> stageSurveys = sheetService.stageXlsxFile(mockFile, false).getStagedRows();

        // Test that dates are formatted the same way as they appear in the sheet.
        assertEquals("12/12/2019", stageSurveys.get(0).getDate());
        assertEquals("12/12/2019", stageSurveys.get(1).getDate());
        assertEquals("12/12/2019", stageSurveys.get(2).getDate());
        assertEquals("14/3/2019", stageSurveys.get(3).getDate());
        assertEquals("14/3/2019", stageSurveys.get(4).getDate());
        assertEquals("14/03/2019", stageSurveys.get(5).getDate());
        assertEquals("14/03/2019", stageSurveys.get(6).getDate());
    }

    @Test
    void blankBuddyInvertsShouldBeZero() throws Exception {
        FileSystemResource file = new FileSystemResource("src/test/resources/sheets/blankBuddyInverts.xlsx");
        MockMultipartFile mockFile = new MockMultipartFile("sheets/blankBuddyInverts.xlsx", file.getInputStream());
        List<StagedRow> stageSurveys = sheetService.stageXlsxFile(mockFile, true).getStagedRows();
        assertEquals("0", stageSurveys.get(1).getInverts());
        assertEquals("0", stageSurveys.get(6).getBuddy());
    }
}
