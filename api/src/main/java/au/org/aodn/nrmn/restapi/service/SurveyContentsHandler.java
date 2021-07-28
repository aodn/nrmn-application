package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.Value;

public class SurveyContentsHandler implements SheetContentsHandler {

    private final List<String> optionalHeaders;
    private List<String> requiredHeaders;

    private ParsedSheet result;
    private String error;

    private StagedRow currentRow;
    private boolean isFirstHeaderRow = false;
    private boolean isDataRow = false;
    private boolean rowHasData = false;
    private HashMap<String, String> columnHeaders = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<>();
    List<StagedRow> stagedRows = new ArrayList<>();
    private Long numEmptyRows = 0L;

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        this.requiredHeaders = requiredHeaders;
        this.optionalHeaders = optionalHeaders;
    }

    public ParsedSheet getResult() {
        return this.result;
    }

    public String getError() {
        return this.error != null ? this.error : this.getResult() == null ? "DATA sheet not found" : null;
    }

    @Override
    public void startRow(int rowNum) {
        isFirstHeaderRow = (rowNum == 0);
        isDataRow = (rowNum > 1);
        rowHasData = false;
        if (isDataRow) {
            currentRow = StagedRow.builder().pos((rowNum - 1) * 1000).build();
            for (String col : requiredHeaders)
                setValue(col, "");
        }
    }

    @Override
    public void endRow(int rowNum) {
        if (isFirstHeaderRow) {
            List<String> errors = new ArrayList<String>();
            List<String> foundHeaders = new ArrayList<String>(columnHeaders.values());
            List<String> missingHeaders = new ArrayList<String>(requiredHeaders);
            missingHeaders.removeAll(foundHeaders);
            if (missingHeaders.size() > 0)
                errors.add("Missing Headers: " + String.join(", ", missingHeaders));
            foundHeaders.removeAll(requiredHeaders);
            foundHeaders.removeAll(optionalHeaders);
            if (foundHeaders.size() > 0)
                errors.add("Unexpected Headers: " + String.join(", ", foundHeaders));
            if (errors.size() > 0)
                error = String.join(". ", errors);
        } else if (isDataRow) {
            if (rowHasData) {
                currentRow.setMeasureJson(new HashMap<>(measureJson));
                this.stagedRows.add(currentRow);
            } else {
                numEmptyRows++;
            }
        }
        measureJson.clear();
    }

    @Override
    public void endSheet() {
        if (stagedRows.size() > 0)
            result = new ParsedSheet(stagedRows, numEmptyRows);
        else
            error = result == null ? "Empty DATA sheet" : null;
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {

        if (result != null)
            return;

        String columnKey = cellReference.replaceAll("[0123456789]", "");
        if (isFirstHeaderRow) {
            columnHeaders.put(columnKey, formattedValue);
        } else if (isDataRow){
            String col = columnHeaders.getOrDefault(columnKey, "");
            boolean cellHasData = StringUtils.isNotEmpty(formattedValue) && !formattedValue.contentEquals("null");
            rowHasData = rowHasData || cellHasData;
            String value = cellHasData ? formattedValue : "";
            setValue(col, value);
        }
    }

    private void setValue(String columnHeader, String formattedValue) {
        switch (columnHeader) {
            case "ID":
                break;
            case "Buddy":
                currentRow.setBuddy(formattedValue);
                break;
            case "Diver":
                currentRow.setDiver(formattedValue);
                break;
            case "Site No.":
                currentRow.setSiteCode(formattedValue);
                break;
            case "Site Name":
                currentRow.setSiteName(formattedValue);
                break;
            case "Latitude":
                currentRow.setLatitude(formattedValue);
                break;
            case "Longitude":
                currentRow.setLongitude(formattedValue);
                break;
            case "Date":
                currentRow.setDate(formattedValue);
                break;
            case "vis":
                currentRow.setVis(formattedValue);
                break;
            case "Direction":
                currentRow.setDirection(formattedValue);
                break;
            case "Time":
                currentRow.setTime(formattedValue);
                break;
            case "P-Qs":
                currentRow.setPqs(formattedValue);
                break;
            case "Depth":
                currentRow.setDepth(formattedValue);
                break;
            case "Method":
                currentRow.setMethod(formattedValue);
                break;
            case "Block":
                currentRow.setBlock(formattedValue);
                break;
            case "Code":
                currentRow.setCode(formattedValue);
                break;
            case "Species":
                currentRow.setSpecies(formattedValue);
                break;
            case "Common name":
                currentRow.setCommonName(formattedValue);
                break;
            case "Total":
                currentRow.setTotal(formattedValue);
                break;
            case "Use InvertSizing":
                currentRow.setIsInvertSizing(formattedValue);
                break;
            case "Inverts":
                currentRow.setInverts(formattedValue);
                break;
            default:
                if (formattedValue.length() > 0 && requiredHeaders.contains(columnHeader)
                        && columnHeader.matches("\\d.*"))
                    measureJson.put(requiredHeaders.indexOf(columnHeader) - requiredHeaders.indexOf("Inverts"),
                            formattedValue);
                break;
        }
    }

    @Value
    public class ParsedSheet {
        private List<StagedRow> stagedRows;
        private Long numEmptyRows;
    }
}