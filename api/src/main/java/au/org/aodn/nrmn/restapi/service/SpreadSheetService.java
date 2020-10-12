package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobEntityRepository;
import au.org.aodn.nrmn.restapi.service.model.HeaderCellIndex;
import au.org.aodn.nrmn.restapi.service.model.SheetWithHeader;
import cyclops.control.Maybe;
import cyclops.control.Validated;
import lombok.val;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SpreadSheetService {

    @Value("${app.excel.headers.short}")
    private List<String> shortHeadersRef;

    @Value("${app.excel.headers.long}")
    private List<String> longHeadersRef;

    @Autowired
    private StagedJobEntityRepository jobRepo;

    public Validated<ErrorInput, SheetWithHeader> validExcelFile(String fileId,
                                                                 Workbook book,
                                                                 Boolean withInvertedSize) {
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
                    return new HeaderCellIndex(defaultFormat.formatCellValue(firstRow.getCell(i), evaluator), i);
                }).filter(head -> !head.getName().equals(""))
                .collect(Collectors.toList());

        val headerByName = headers.stream()
                .map(HeaderCellIndex::getName).collect(Collectors.toList());

        val missingHeaders = refHeader.stream()
                .filter(refHead -> headerByName.stream()
                        .noneMatch(name ->  name.equals(refHead)))
                .collect(Collectors.toList());

        if (missingHeaders.size() > 0) {
            return Validated.invalid(new ErrorInput(
                            "Missing Headers:" +
                                    missingHeaders.stream().reduce("", (acc, elem) -> acc + "," + elem), "headers"
                    )
            );
        }

        return Validated.valid(new SheetWithHeader(fileId, headers, sheet));
    }

    public List<StagedSurveyEntity> sheets2Staged(SheetWithHeader dataSheet) {
        val formatter = new SimpleDateFormat("dd-MMM-yyyy");
        val eval = new XSSFFormulaEvaluator((XSSFWorkbook) dataSheet.getSheet().getWorkbook());
        val fmt = new DataFormatter();

        val headerMap = dataSheet
                .getHeader()
                .stream()
                .collect(Collectors.toMap(HeaderCellIndex::getName, HeaderCellIndex::getIndex));

        val headerNum = dataSheet.getHeader().stream().filter(h ->
                Maybe.attempt(() -> Float.parseFloat(h.getName())).isPresent()
        ).collect(Collectors.toList());

        val stagedJob = jobRepo.save(
                new StagedJobEntity(
                        dataSheet.getFileId(),
                        StatusJobType.PENDING,
                        SourceJobType.FILE, new HashMap<>()
                ));

        List<StagedSurveyEntity> stagedSurveys = IntStream
                .range(dataSheet.getSheet().getFirstRowNum(), dataSheet.getSheet().getLastRowNum())
                .mapToObj(index -> {
                    val row = dataSheet.getSheet().getRow(index);
                    val stagedSurvey = new StagedSurveyEntity();
                    stagedSurvey.setDiver(_getCellValue(row.getCell(headerMap.get("Diver")), eval, fmt));
                    stagedSurvey.setBuddy(_getCellValue(row.getCell(headerMap.get("Buddy")), eval, fmt));
                    stagedSurvey.setSiteNo(_getCellValue(row.getCell(headerMap.get("Site No.")), eval, fmt));
                    stagedSurvey.setSiteName(_getCellValue(row.getCell(headerMap.get("Site Name")), eval, fmt));
                    stagedSurvey.setLatitude(safeDouble(_getCellValue(row.getCell(headerMap.get("Latitude")), eval, fmt)));
                    stagedSurvey.setLatitude(safeDouble(_getCellValue(row.getCell(headerMap.get("Latitude")), eval, fmt)));
                    val date = Maybe.attempt(() ->
                            formatter.parse(_getCellValue(row.getCell(headerMap.get("Date")), eval, fmt))).orElseGet(() -> null);
                    stagedSurvey.setDate(date);
                    stagedSurvey.setVis(safeInt(_getCellValue(row.getCell(headerMap.get("vis")), eval, fmt)));
                    stagedSurvey.setDirection(_getCellValue(row.getCell(headerMap.get("Direction")), eval, fmt));
                    stagedSurvey.setTime(safeDouble(_getCellValue(row.getCell(headerMap.get("Time")), eval, fmt)));
                    stagedSurvey.setPQs(safeInt(_getCellValue(row.getCell(headerMap.get("P-Qs")), eval, fmt)));
                    stagedSurvey.setDepth(safeDouble(_getCellValue(row.getCell(headerMap.get("P-Qs")), eval, fmt)));
                    stagedSurvey.setMethod(safeInt(_getCellValue(row.getCell(headerMap.get("Method")), eval, fmt)));
                    stagedSurvey.setBlock(safeInt(_getCellValue(row.getCell(headerMap.get("Block")), eval, fmt)));
                    stagedSurvey.setCode(_getCellValue(row.getCell(headerMap.get("Code")), eval, fmt));
                    stagedSurvey.setSpecies(_getCellValue(row.getCell(headerMap.get("Species")), eval, fmt));
                    stagedSurvey.setCmmonName(_getCellValue(row.getCell(headerMap.get("Common name")), eval, fmt));
                    stagedSurvey.setTotal(safeInt(_getCellValue(row.getCell(headerMap.get("Total")), eval, fmt)));
                    stagedSurvey.setInverts(safeInt(_getCellValue(row.getCell(headerMap.get("Inverts")), eval, fmt)));
                    if (dataSheet.getHeader().size() == longHeadersRef.size()) {
                        stagedSurvey.setM2InvertSizingSpecies(safeInt(_getCellValue(row.getCell(headerMap.get("M2 Invert Sizing Species")), eval, fmt)) == 1);
                        stagedSurvey.setL5(safeInt(_getCellValue(row.getCell(headerMap.get("L5")), eval, fmt)));
                        stagedSurvey.setL95(safeInt(_getCellValue(row.getCell(headerMap.get("L95")), eval, fmt)));
                        stagedSurvey.setIsInvertSizing(safeInt(_getCellValue(row.getCell(headerMap.get("Use InvertSizing")), eval, fmt)) == 1);
                        stagedSurvey.setLmax(safeInt(_getCellValue(row.getCell(headerMap.get("Lmax")), eval, fmt)));
                    }

                    val measureJson = new HashMap<String, Double>();
                    headerNum.forEach(header -> {
                        val cellValue = safeDouble(_getCellValue(row.getCell(header.getIndex()), eval, fmt));
                        if (cellValue > 0) ;
                        measureJson.put(header.getName(), cellValue);
                    });
                    stagedSurvey.setMeasureJson(measureJson);
                    stagedSurvey.setStagedJob(stagedJob);
                    return stagedSurvey;
                }).collect(Collectors.toList());

        return stagedSurveys;
    }


    private Double safeDouble(String target) {
        return Maybe.attempt(() -> safeDouble(target)).orElseGet(() -> 0D);
    }

    private Integer safeInt(String target) {
        return Maybe.attempt(() -> safeInt(target)).orElseGet(() -> 0);
    }

    private String _getCellValue(Cell cell, XSSFFormulaEvaluator evaluator, DataFormatter formater) {
        evaluator.evaluate(cell);
        return formater.formatCellValue(cell, evaluator);
    }
}
