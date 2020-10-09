package au.org.aodn.nrmn.restapi.service;

import antlr.collections.impl.IntRange;
import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import cyclops.control.Validated;
import lombok.val;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpreadSheetService {

    @Value("${app.excel.headers.short}")
    private List<String> shortHeadersRef;

    @Value("${app.excel.headers.long}")
    private List<String> longHeadersRef;

    public Validated<ErrorInput, Sheet> validExcelFile(Workbook book, Boolean withInvertedSize) {
        val evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) book);
        DataFormatter defaultFormat = new DataFormatter();

        if (book.getSheetIndex("DATA") < 0) {
            return Validated.invalid(new ErrorInput("DATA sheet not found", "excel"));
        }
        val refHeader = (withInvertedSize) ? longHeadersRef : shortHeadersRef;
        val sheet = book.getSheet("DATA");
        val indexFirstRow = sheet.getFirstRowNum();
        val firstRow = sheet.getRow(indexFirstRow);
        val headers = IntStream
                .range(firstRow.getFirstCellNum(), sheet.getLastRowNum())
                .mapToObj(i -> {
                    evaluator.evaluate(firstRow.getCell(i));
                   return defaultFormat.formatCellValue(firstRow.getCell(i), evaluator);
                })
                .collect(Collectors.toList());
        val missingHeaders = refHeader.stream().filter(head -> !headers.contains(head)).collect(Collectors.toList());

        if (missingHeaders.size() > 0) {
            return Validated.invalid(new ErrorInput(
                            "Missing Headers:" +
                                    missingHeaders.stream().reduce("", (acc, elem) -> acc + "," + elem), "headers"
                    )
            );
        }
        return Validated.valid(sheet);

    }

    public List<StagedSurveyEntity>  sheets2Staged(List<Sheet> sheets) {
        //todo add the transformation;
        return Collections.emptyList();
    }
}
