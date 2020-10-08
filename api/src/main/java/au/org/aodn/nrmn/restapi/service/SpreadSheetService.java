package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import cyclops.control.Validated;
import lombok.val;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpreadSheetService {

    @Value("${app.excel.headers.short}")
    public List<String> shortHeadersRef;

    @Value("${app.excel.headers.long}")
    public List<String> longHeadersRef;

    public Validated<ErrorInput, Sheet> validExcelFile(Workbook book, Boolean withInvertedSize) {
        if (book.getSheetIndex("DATA") > 0) {
            return Validated.invalid(new ErrorInput("DATA sheet not found", "excel"));
        }
        val refHeader = (withInvertedSize) ? longHeadersRef : shortHeadersRef
        val sheet = book.getSheet("DATA");
        val indexFirstRow = sheet.getFirstRowNum();
        val firstRow = sheet.getRow(indexFirstRow);
        val headers = IntStream
                .range(firstRow.getFirstCellNum(), sheet.getLastRowNum())
                .mapToObj(i -> firstRow.getCell(i).getStringCellValue()).collect(Collectors.toList());
        val missingHeaders = headers.stream().filter(head -> refHeader.contains(head)).collect(Collectors.toList())

        if (missingHeaders.size() > 0) {
            return Validated.invalid(new ErrorInput(
                            "Missing Headers:" +
                                    missingHeaders.stream().reduce("", (acc, elem) -> acc + "," + elem), "headers"
                    )
            );
        }
        return Validated.valid(sheet);

    }
}
