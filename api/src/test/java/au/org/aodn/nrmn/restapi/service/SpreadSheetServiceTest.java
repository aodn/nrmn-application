package au.org.aodn.nrmn.restapi.service;

import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpreadSheetServiceTest {
    @Autowired
    SpreadSheetService sheetService;

    @Test
    public void correctShortheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");
        val validSheet =
                sheetService.validExcelFile("idfile", new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isValid());
    }

    @Test
    public void correctLongheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctLongHeader.xlsx");
        val validSheet =
                sheetService.validExcelFile("idfile",new XSSFWorkbook(rottnestInput), true);
        assertTrue(validSheet.isValid());
    }

    @Test
    public void missingDataSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingDataSheet.xlsx");
        val validSheet =
                sheetService.validExcelFile("idfile", new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isInvalid());
    }

    @Test
    public void missingColunmsSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingColumnsHeader.xlsx");
        val validSheet = sheetService.validExcelFile("idfile",new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isInvalid());
    }
}