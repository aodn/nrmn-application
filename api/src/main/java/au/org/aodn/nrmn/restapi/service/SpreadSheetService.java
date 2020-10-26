package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.service.model.HeaderCellIndex;
import au.org.aodn.nrmn.restapi.service.model.SheetWithHeader;
import cyclops.control.Future;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Validated;
import lombok.val;
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

import java.text.SimpleDateFormat;
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

    public List<StagedSurvey> sheets2Staged(SheetWithHeader dataSheet) {
        val formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        val eval = new XSSFFormulaEvaluator((XSSFWorkbook) dataSheet.getSheet().getWorkbook());
        val fmt = new DataFormatter();

        val headerMap = dataSheet
                .getHeader()
                .stream()
                .collect(Collectors.toMap(HeaderCellIndex::getName, HeaderCellIndex::getIndex));

        val headerNum = dataSheet.getHeader().stream().filter(h ->
                Maybe.attempt(() -> Float.parseFloat(h.getName())).isPresent()
        ).collect(Collectors.toList());


        List<StagedSurvey> stagedSurveys = IntStream
                .range(2, dataSheet.getSheet().getPhysicalNumberOfRows())
                .filter(i ->
                        Maybe.attempt(() ->
                                dataSheet.getSheet().getRow(i).isFormatted() &&
                                        !_getCellValue(dataSheet.getSheet().getRow(i).getCell(headerMap.get("ID")), eval, fmt).equals("")
                        ).orElseGet(() -> false)
                ).mapToObj(index -> {
                    val row = dataSheet.getSheet().getRow(index);
                    val stagedSurvey = new StagedSurvey();
                    stagedSurvey.setDiver(_getCellValue(row.getCell(headerMap.get("Diver")), eval, fmt));
                    stagedSurvey.setBuddy(_getCellValue(row.getCell(headerMap.get("Buddy")), eval, fmt));
                    stagedSurvey.setSiteNo(_getCellValue(row.getCell(headerMap.get("Site No.")), eval, fmt));
                    stagedSurvey.setSiteName(_getCellValue(row.getCell(headerMap.get("Site Name")), eval, fmt));
                    stagedSurvey.setLatitude(safeDouble(_getCellValue(row.getCell(headerMap.get("Latitude")), eval, fmt)));
                    stagedSurvey.setLongitude(safeDouble(_getCellValue(row.getCell(headerMap.get("Longitude")), eval, fmt)));
                    val date = Maybe.attempt(() -> {
                        val time = _getCellValue(row.getCell(headerMap.get("Time")), eval, fmt);
                        val dayMonthYear = _getCellValue(row.getCell(headerMap.get("Date")), eval, fmt);
                        return formatter.parse(dayMonthYear + " " + time);
                    }).orElseGet(() -> null);
                    stagedSurvey.setDate(date);
                    stagedSurvey.setVis(safeInt(_getCellValue(row.getCell(headerMap.get("vis")), eval, fmt)));
                    stagedSurvey.setDirection(_getCellValue(row.getCell(headerMap.get("Direction")), eval, fmt));
                    stagedSurvey.setPQs(_getCellValue(row.getCell(headerMap.get("P-Qs")), eval, fmt));
                    stagedSurvey.setDepth(safeDouble(_getCellValue(row.getCell(headerMap.get("Depth")), eval, fmt)));
                    stagedSurvey.setMethod(safeInt(_getCellValue(row.getCell(headerMap.get("Method")), eval, fmt)));
                    stagedSurvey.setBlock(safeInt(_getCellValue(row.getCell(headerMap.get("Block")), eval, fmt)));
                    stagedSurvey.setCode(_getCellValue(row.getCell(headerMap.get("Code")), eval, fmt));
                    stagedSurvey.setSpecies(_getCellValue(row.getCell(headerMap.get("Species")), eval, fmt));
                    stagedSurvey.setCmmonName(_getCellValue(row.getCell(headerMap.get("Common name")), eval, fmt));
                    stagedSurvey.setTotal(safeInt(_getCellValue(row.getCell(headerMap.get("Total")), eval, fmt)));
                    stagedSurvey.setInverts(safeInt(_getCellValue(row.getCell(headerMap.get("Inverts")), eval, fmt)));
                    if (dataSheet.getHeader().size() == longHeadersRef.size()) {
                        stagedSurvey.setM2InvertSizingSpecies(_getCellValue(row.getCell(headerMap.get("M2 Invert Sizing Species")), eval, fmt).equals("Yes"));
                        stagedSurvey.setL5(safeInt(_getCellValue(row.getCell(headerMap.get("L5")), eval, fmt)));
                        stagedSurvey.setL95(safeInt(_getCellValue(row.getCell(headerMap.get("L95")), eval, fmt)));
                        stagedSurvey.setIsInvertSizing(_getCellValue(row.getCell(headerMap.get("Use InvertSizing")), eval, fmt).equals("Yes"));
                        stagedSurvey.setLmax(safeInt(_getCellValue(row.getCell(headerMap.get("Lmax")), eval, fmt)));
                    }

                    val measureJson = new HashMap<String, Integer>();
                    headerNum.forEach(header -> {
                        val cellValue = safeInt(_getCellValue(row.getCell(header.getIndex()), eval, fmt));
                        if (cellValue != null && cellValue > 0)
                            measureJson.put(header.getName(), cellValue);
                    });
                    stagedSurvey.setMeasureJson(measureJson);
                    return stagedSurvey;
                }).collect(Collectors.toList());
        return stagedSurveys;
    }


    private Double safeDouble(String target) {
        return Maybe.attempt(() -> Double.parseDouble(target)).orElseGet(() -> null);
    }

    private Integer safeInt(String target) {
        return Maybe.attempt(() -> Integer.parseInt(target)).orElseGet(() -> null);
    }

    private String _getCellValue(Cell cell, XSSFFormulaEvaluator evaluator, DataFormatter formater) {
        evaluator.evaluate(cell);
        return formater.formatCellValue(cell, evaluator);
    }
}
