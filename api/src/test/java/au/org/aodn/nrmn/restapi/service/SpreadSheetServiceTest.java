package au.org.aodn.nrmn.restapi.service;

import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("cicd")
class SpreadSheetServiceTest {
    @Autowired
    SpreadSheetService sheetService;

    @Test
    public void correctShortheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctShortHeader.xlsx");
        val validSheet =
                sheetService.validatedExcelFile("idfile", new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isValid());
    }

    @Test
    public void correctLongheaderShouldBeValid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/correctLongHeader.xlsx");
        val validSheet =
                sheetService.validatedExcelFile("idfile",new XSSFWorkbook(rottnestInput), true);
        assertTrue(validSheet.isValid());
    }

    @Test
    public void missingDataSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingDataSheet.xlsx");
        val validSheet =
                sheetService.validatedExcelFile("idfile", new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isInvalid());
    }

    @Test
    public void missingColunmsSheethouldBeInvalid() throws Exception {
        InputStream rottnestInput = getClass()
                .getClassLoader().getResourceAsStream("sheets/missingColumnsHeader.xlsx");
        val validSheet = sheetService.validatedExcelFile("idfile",new XSSFWorkbook(rottnestInput), false);
        assertTrue(validSheet.isInvalid());
    }


    @Test void  validFileShouldBeCorrectlyTransformToStageSurvey() throws Exception {
        val file3 = new FileSystemResource("src/test/resources/sheets/correctShortHeader3.xlsx");
        val sheetWithHeader = sheetService.validatedExcelFile("testFile-1234561",new XSSFWorkbook(file3.getInputStream()), false).orElseGet(() -> null);
        val stageSurveys = sheetService.sheets2Staged(sheetWithHeader);
        assertEquals(stageSurveys.size(), 2);
         val obs1 = stageSurveys.get(0);

         //test Double
         assertEquals(obs1.getLatitude(), -41.253706);
        assertEquals(obs1.getLongitude(), 148.339749);
        //test Map filling
        assertEquals(obs1.getMeasureJson().size(), 4);
        assertEquals(obs1.getMeasureJson().get("162.5"), 4);
        // test Macro
        assertEquals(obs1.getSpecies(), "Caesioperca rasor");


    }
}