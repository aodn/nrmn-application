package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.service.model.HeaderCellIndex;
import au.org.aodn.nrmn.restapi.service.model.SheetWithHeader;
import cyclops.control.Future;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
public class SpreadSheetService {

    private Logger log = LoggerFactory.getLogger(SpreadSheetService.class);

    @Value("${app.excel.headers.short}")
    private List<String> shortHeadersRef;

    @Value("${app.excel.headers.long}")
    private List<String> longHeadersRef;

    @Autowired
    private S3IO s3client;


    public Validated<ErrorInput, SheetWithHeader>
    validatedExcelFile(String fileId,
                       MultipartFile excelFile,
                       Boolean withInvertedSize) {


        val bookOpt = Try.withResources(
                excelFile::getInputStream, (in) -> {
                    XSSFWorkbook res = new XSSFWorkbook(in);
                    in.close();
                    return res;
                }).onFail(e ->
                log.error(String.format("Error while reading the excelFile:" + e.getMessage()))
        ).toOptional();

        if (!bookOpt.isPresent()) {
            return Validated.invalid(
                    new ErrorInput("Error while opening the file, Excel 2007 or above required", "file")
            );
        }
        val book = bookOpt.get();
        val evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) book);
        DataFormatter defaultFormat = new DataFormatter();

        if (book.getSheetIndex("DATA") < 0) {
            return Validated.invalid(new ErrorInput("DATA sheet not found", "excel"));
        }

        val refHeader = (withInvertedSize) ? longHeadersRef : shortHeadersRef;
        val sheet = book.getSheet("DATA");
        val indexFirstRow = sheet.getFirstRowNum();
        val firstRow = sheet.getRow(indexFirstRow);
        List<HeaderCellIndex> headers = IntStream
                .range(firstRow.getFirstCellNum(), firstRow.getLastCellNum())
                .mapToObj(i -> {
                    evaluator.evaluate(firstRow.getCell(i));
                    String value = defaultFormat.formatCellValue(firstRow.getCell(i), evaluator);
                    return new HeaderCellIndex(value, i);
                }).filter(head -> !head.getName().equals(""))
                .collect(Collectors.toList());

        List<String> headerByName = headers.stream()
                .map(head -> head.getName()).collect(Collectors.toList());

        List<String> missingHeaders = refHeader.stream()
                .filter(refHead -> headerByName.stream()
                        .noneMatch(name -> name.equals(refHead)))
                .collect(Collectors.toList());

        if (missingHeaders.size() > 0) {
            return Validated.invalid(new ErrorInput(
                            "Missing Headers:" +
                                    missingHeaders.stream().reduce("", (acc, elem) -> acc + "," + elem), "headers"
                    )
            );
        }
        Future.of(() -> s3client.write("/raw-survey/" + fileId + ".xlsx", excelFile));
        return Validated.valid(new SheetWithHeader(fileId, headers, sheet));
    }


        public List<StagedRow> sheets2Staged (SheetWithHeader dataSheet){
            val eval = new XSSFFormulaEvaluator((XSSFWorkbook) dataSheet.getSheet().getWorkbook());
            val fmt = new DataFormatter();

            val headerMap = dataSheet
                    .getHeader()
                    .stream()
                    .collect(Collectors.toMap(HeaderCellIndex::getName, HeaderCellIndex::getIndex));

            val headerNum = dataSheet.getHeader().stream().filter(h ->
                    Maybe.attempt(() -> Float.parseFloat(h.getName())).isPresent()
            ).collect(Collectors.toList());


            List<StagedRow> stagedRows = IntStream
                    .range(2, dataSheet.getSheet().getPhysicalNumberOfRows())
                    .filter(i ->
                            Maybe.attempt(() ->
                                    dataSheet.getSheet().getRow(i).isFormatted() &&
                                            !_getCellValue(dataSheet.getSheet().getRow(i).getCell(headerMap.get("ID")), eval, fmt).equals("")
                            ).orElseGet(() -> false)
                    ).mapToObj(index -> {
                        val row = dataSheet.getSheet().getRow(index);
                        val stagedRow = new StagedRow();
                        stagedRow.setDiver(_getCellValue(row.getCell(headerMap.get("Diver")), eval, fmt));
                        stagedRow.setBuddy(_getCellValue(row.getCell(headerMap.get("Buddy")), eval, fmt));
                        stagedRow.setSiteNo(_getCellValue(row.getCell(headerMap.get("Site No.")), eval, fmt));
                        stagedRow.setSiteName(_getCellValue(row.getCell(headerMap.get("Site Name")), eval, fmt));
                        stagedRow.setLatitude(_getCellValue(row.getCell(headerMap.get("Latitude")), eval, fmt));
                        stagedRow.setLongitude(_getCellValue(row.getCell(headerMap.get("Longitude")), eval, fmt));
                        stagedRow.setTime(_getCellValue(row.getCell(headerMap.get("Time")), eval, fmt));
                        stagedRow.setDate(_getCellValue(row.getCell(headerMap.get("Date")), eval, fmt));
                        stagedRow.setVis((_getCellValue(row.getCell(headerMap.get("vis")), eval, fmt)));
                        stagedRow.setDirection(_getCellValue(row.getCell(headerMap.get("Direction")), eval, fmt));
                        stagedRow.setPqs(_getCellValue(row.getCell(headerMap.get("P-Qs")), eval, fmt));
                        stagedRow.setDepth(_getCellValue(row.getCell(headerMap.get("Depth")), eval, fmt));
                        stagedRow.setMethod(_getCellValue(row.getCell(headerMap.get("Method")), eval, fmt));
                        stagedRow.setBlock((_getCellValue(row.getCell(headerMap.get("Block")), eval, fmt)));
                        stagedRow.setCode(_getCellValue(row.getCell(headerMap.get("Code")), eval, fmt));
                        stagedRow.setSpecies(_getCellValue(row.getCell(headerMap.get("Species")), eval, fmt));
                        stagedRow.setCommonName(_getCellValue(row.getCell(headerMap.get("Common name")), eval, fmt));
                        stagedRow.setTotal(_getCellValue(row.getCell(headerMap.get("Total")), eval, fmt));
                        stagedRow.setInverts(_getCellValue(row.getCell(headerMap.get("Inverts")), eval, fmt));
                        if (dataSheet.getHeader().size() == longHeadersRef.size()) {
                            stagedRow.setM2InvertSizingSpecies(_getCellValue(row.getCell(headerMap.get("M2 Invert Sizing Species")), eval, fmt).equals("Yes"));
                            stagedRow.setL5(_getCellValue(row.getCell(headerMap.get("L5")), eval, fmt));
                            stagedRow.setL95(_getCellValue(row.getCell(headerMap.get("L95")), eval, fmt));
                            stagedRow.setIsInvertSizing(_getCellValue(row.getCell(headerMap.get("Use InvertSizing")), eval, fmt));
                            stagedRow.setLmax(_getCellValue(row.getCell(headerMap.get("Lmax")), eval, fmt));
                        }

                        val measureJson = new HashMap<String, String>();
                        headerNum.forEach(header -> {
                            val cellValue = _getCellValue(row.getCell(header.getIndex()), eval, fmt);
                            if (cellValue != null && !StringUtils.isEmpty(cellValue))
                                measureJson.put(header.getName(), cellValue);
                        });
                        stagedRow.setMeasureJson(measureJson);
                        return stagedRow;
                    }).collect(Collectors.toList());

            return stagedRows;
        }

        private String _getCellValue (Cell cell, XSSFFormulaEvaluator evaluator, DataFormatter formater){
            evaluator.evaluate(cell);
            return formater.formatCellValue(cell, evaluator);
        }
    }
