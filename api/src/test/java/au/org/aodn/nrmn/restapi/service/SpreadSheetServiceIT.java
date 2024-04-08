package au.org.aodn.nrmn.restapi.service;

import java.io.InputStream;
import java.util.List;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.enums.SurveyField;
import au.org.aodn.nrmn.restapi.service.upload.SpreadSheetService;
import au.org.aodn.nrmn.restapi.service.upload.SurveyContentsHandler.ParsedSheet;
import au.org.aodn.nrmn.restapi.controller.StagedJobController;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class SpreadSheetServiceIT {

    @Autowired
    SpreadSheetService sheetService;

    @Autowired
    StagedJobController stagedJobController;

    @Test
    public void correctShortHeadersShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");

        ParsedSheet validSheet = sheetService.stageXlsxFile(
                new MockMultipartFile("sheets/correctShortHeader.xlsx", rottnestInput), false);
        assertTrue(validSheet != null);
    }

    @Test
    public void correctLongHeadersShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/correctLongHeader.xlsx");
        ParsedSheet validSheet = sheetService.stageXlsxFile(
                new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), true);
        assertTrue(validSheet != null);
    }

    @Test
    public void correctLongHeadersIngestedAsShortShouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/correctLongHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/correctLongHeader.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        // Reject the sheet for having unexpected headers
        assertTrue(StringUtils.isNotEmpty(error));
    }

    @Test
    public void missingDataSheetShouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingDataSheet.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/missingDataSheet.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(StringUtils.isNotEmpty(error));
    }

    @Test
    public void missingColumnsSheetShouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingColumnsHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/missingColumnsHeader.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(StringUtils.isNotEmpty(error));
    }

    @Test
    public void mismatchedColumnsSheetShouldBeInvalid() throws Exception {

        InputStream rottnestInput = getClass().getClassLoader()
                .getResourceAsStream("sheets/mismatchedColumnsHeader.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/mismatchedColumnsHeader.xlsx", rottnestInput),
                    false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(StringUtils.isNotEmpty(error));
    }

    @Test
    public void missingInvertsRowShouldBeInvalid() throws Exception {

        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingRow2.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/missingRow2.xlsx", rottnestInput), false);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(StringUtils.isNotEmpty(error));
    }

    @Test
    public void shortInvertsRowShouldBeInvalid() throws Exception {

        InputStream rottnestInput = getClass().getClassLoader().getResourceAsStream("sheets/missingRshortRow2w2.xlsx");
        String error = null;
        try {
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/shortRow2.xlsx", rottnestInput), true);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertTrue(StringUtils.isNotEmpty(error));
    }
    /**
     * Excel row2 must be blank according to the import file spec, if not blank throw exception
     */
    @Test
    void rejectFileWhenRow2IsNotEmpty() {
        Exception e = assertThrows(Exception.class, () -> {
            FileSystemResource file3 = new FileSystemResource("src/test/resources/sheets/row2NotEmpty.xlsx");
            sheetService.stageXlsxFile(new MockMultipartFile("sheets/row2NotEmpty.xlsx", file3.getInputStream()), false);
        });
        assertEquals("Cell range A2-G2 is not blank.", e.getMessage(), "Non empty row 2 warning");
    }

    @Test
    void validFileShouldBeCorrectlyTransformToStageSurvey() throws Exception {
        FileSystemResource file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
        ParsedSheet parsedSheet = sheetService
                .stageXlsxFile(new MockMultipartFile("sheets/correctShortHeader3.xlsx", file3.getInputStream()), false);

        List<StagedRow> stageSurveys = parsedSheet.getStagedRows();
        assertEquals(23, stageSurveys.size());
        StagedRow obs1 = stageSurveys.get(0);

        // Test Double
        assertEquals("-41.25371", obs1.getLatitude());
        assertEquals("148.33975", obs1.getLongitude());

        // Test Map filling
        assertEquals(obs1.getMeasureJson().size(), 4);
        assertEquals("5", obs1.getMeasureJson().get(SurveyField.TWO_FIVE.getPosition()), "Row 3 measurement 2.5 should be");
        assertEquals("2", obs1.getMeasureJson().get(SurveyField.TEN.getPosition()), "Row 3 measurement 10 should be");
        assertEquals("5", obs1.getMeasureJson().get(SurveyField.TWELVE_FIVE.getPosition()), "Row 3 measurement 12.5 should be");
        assertEquals("4", obs1.getMeasureJson().get(SurveyField.HUNDRED_SIXTY_TWO_FIVE.getPosition()), "Row 3 measurement 162.5 should be");
        assertFalse(obs1.getMeasureJson().containsKey(SurveyField.TWO_HUNDRED.getPosition()), "Row 3 no measurement at 200");

        // Test Macro
        assertEquals(obs1.getSpecies(), "Caesioperca rasor");

        // Test second row
        StagedRow obs2 = stageSurveys.get(1);
        assertEquals("-41.25371", obs2.getLatitude(), "Row 4 latitude");
        assertEquals("148.33975", obs2.getLongitude(), "Row 4 longitude");

        assertEquals(obs1.getMeasureJson().size(), 4);
        assertFalse(obs2.getMeasureJson().containsKey(SurveyField.TWO_FIVE.getPosition()), "Row 4 no measurement at 2.5");
        assertEquals("3", obs2.getMeasureJson().get(SurveyField.TEN.getPosition()), "Row 4 measurement 10 should be");
        assertEquals("5", obs2.getMeasureJson().get(SurveyField.HUNDRED_TWELVE_FIVE.getPosition()), "Row 4 measurement 112.5 should be");
        assertEquals("3", obs2.getMeasureJson().get(SurveyField.TWO_HUNDRED.getPosition()), "Row 4 measurement 200 should be");
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

    @Test
    void locationShouldBeRoundedTo5Decimals() throws Exception {
        FileSystemResource file = new FileSystemResource("src/test/resources/sheets/locationFormat.xlsx");
        MockMultipartFile mockFile = new MockMultipartFile("sheets/locationFormat.xlsx", file.getInputStream());
        List<StagedRow> stageSurveys = sheetService.stageXlsxFile(mockFile, false).getStagedRows();

        assertEquals("-123.12346", stageSurveys.get(0).getLatitude());
        assertEquals("12.1234", stageSurveys.get(0).getLongitude());

        assertEquals("-12.12345", stageSurveys.get(3).getLatitude());
        assertEquals("12.12346", stageSurveys.get(3).getLongitude());

        assertEquals("-1", stageSurveys.get(6).getLatitude());
        assertEquals("1", stageSurveys.get(6).getLongitude());
    }

    @Test
    void duplicateRowsShouldBeRemoved() throws Exception {
        FileSystemResource file = new FileSystemResource("src/test/resources/sheets/duplicateCheck.xlsx");
        MockMultipartFile mockFile = new MockMultipartFile("sheets/duplicateCheck.xlsx", file.getInputStream());
        var parsedSheet = sheetService.stageXlsxFile(mockFile, true);
        var validRows = stagedJobController.getRowsToSave(parsedSheet);
        assertEquals(20, validRows.size());
    }

    /** This test is for:
     * When adding new jobs, only rows (with "total=0" and duplicate more than 3 times) at the end of the sheet should be
     * removed. Rows (with "total=0" and duplicate more than 3 times) are not at the end of the sheet should be kept.
     *
     * Because the ids are multiplied by 1000, all the id here will be like this: 464000.
     * @throws Exception
     */
    @Test
    void removeBottomDuplicateRowsOnly() throws Exception {

        // Normal test case, remove the bottom 6 duplicate rows (keep one of them because this is valid row)
        FileSystemResource file = new FileSystemResource("src/test/resources/sheets/removeDuplicateBottomRows.xlsx");
        MockMultipartFile mockFile = new MockMultipartFile("sheets/removeDuplicateBottomRows.xlsx", file.getInputStream());
        var parsedSheet = sheetService.stageXlsxFile(mockFile, false);
        var validRows = stagedJobController.getRowsToSave(parsedSheet);
        assertEquals(487, validRows.size());
        assertTrue(validRows.stream().anyMatch(row -> row.getId() == 464000));
        assertTrue(validRows.stream().anyMatch(row -> row.getId() == 465000));
        assertTrue(validRows.stream().anyMatch(row -> row.getId() == 466000));
        assertTrue(validRows.stream().anyMatch(row -> row.getId() == 467000));
        assertTrue(validRows.stream().anyMatch(row -> row.getId() == 468000));

        // if the `total` column of the last row is not 0, no rows should be removed
        FileSystemResource file1 = new FileSystemResource("src/test/resources/sheets/removeDuplicateBottomRows-lastTotalNot0.xlsx");
        MockMultipartFile mockFile1 = new MockMultipartFile("sheets/removeDuplicateBottomRows-lastTotalNot0.xlsx", file1.getInputStream());
        var parsedSheet1 = sheetService.stageXlsxFile(mockFile1, false);
        var validRows1 = stagedJobController.getRowsToSave(parsedSheet1);
        assertEquals(493, validRows1.size());
        assertTrue(validRows1.stream().anyMatch(row -> row.getId() == 464000));
        assertTrue(validRows1.stream().anyMatch(row -> row.getId() == 465000));
        assertTrue(validRows1.stream().anyMatch(row -> row.getId() == 466000));
        assertTrue(validRows1.stream().anyMatch(row -> row.getId() == 467000));
        assertTrue(validRows1.stream().anyMatch(row -> row.getId() == 468000));

        // if the last row is id=486 (no duplicate rows at the bottom)
        FileSystemResource file2 = new FileSystemResource("src/test/resources/sheets/removeDuplicateBottomRows-lastId486.xlsx");
        MockMultipartFile mockFile2 = new MockMultipartFile("sheets/removeDuplicateBottomRows-lastId486.xlsx", file2.getInputStream());
        var parsedSheet2 = sheetService.stageXlsxFile(mockFile2, false);
        var validRows2 = stagedJobController.getRowsToSave(parsedSheet2);
        assertEquals(486, validRows2.size());
        assertTrue(validRows2.stream().anyMatch(row -> row.getId() == 464000));
        assertTrue(validRows2.stream().anyMatch(row -> row.getId() == 465000));
        assertTrue(validRows2.stream().anyMatch(row -> row.getId() == 466000));
        assertTrue(validRows2.stream().anyMatch(row -> row.getId() == 467000));
        assertTrue(validRows2.stream().anyMatch(row -> row.getId() == 468000));

        // if the last row is id=490 (duplicate rows at the bottom)
        FileSystemResource file3 = new FileSystemResource("src/test/resources/sheets/removeDuplicateBottomRows-lastId490.xlsx");
        MockMultipartFile mockFile3 = new MockMultipartFile("sheets/removeDuplicateBottomRows-lastId490.xlsx", file3.getInputStream());
        var parsedSheet3 = sheetService.stageXlsxFile(mockFile3, false);
        var validRows3 = stagedJobController.getRowsToSave(parsedSheet3);
        assertEquals(487, validRows3.size());
        assertTrue(validRows3.stream().anyMatch(row -> row.getId() == 464000));
        assertTrue(validRows3.stream().anyMatch(row -> row.getId() == 465000));
        assertTrue(validRows3.stream().anyMatch(row -> row.getId() == 466000));
        assertTrue(validRows3.stream().anyMatch(row -> row.getId() == 467000));
        assertTrue(validRows3.stream().anyMatch(row -> row.getId() == 468000));

    }

}
