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
    private boolean isHeaderRow = false;
    private HashMap<String, String> columnHeaders = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<>();
    List<StagedRow> stagedRows = new ArrayList<>();

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
        isHeaderRow = (rowNum == 0);
        if (!isHeaderRow) {
            currentRow = StagedRow.builder().pos((rowNum - 1) * 1000).build();
            for (String col : requiredHeaders)
                setValue(col, "");
        }
    }

    @Override
    public void endRow(int rowNum) {
        if (isHeaderRow) {
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
        } else {
            if (rowNum > 1) {
                currentRow.setMeasureJson(new HashMap<Integer, String>(measureJson));
                this.stagedRows.add(currentRow);
            }
        }
        measureJson.clear();
    }

    @Override
    public void endSheet() {
        if (stagedRows.size() > 0)
            result = new ParsedSheet(stagedRows);
        else
            error = result == null ? "Empty DATA sheet" : null;
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {

        if (result != null)
            return;

        String columnKey = cellReference.replaceAll("[0123456789]", "");
        if (isHeaderRow) {
            columnHeaders.put(columnKey, formattedValue);
        } else {
            String col = columnHeaders.getOrDefault(columnKey, "");
            boolean cellHasData = StringUtils.isNotEmpty(formattedValue) && !formattedValue.contentEquals("null");
            String value = cellHasData ? formattedValue : "";
            setValue(col, value);
        }
    }

    private void setValue(String columnHeader, String formattedValue) {
        String value = formattedValue != null ? formattedValue.trim() : "";
        switch (columnHeader) {
            case "ID":
                currentRow.setId(Long.valueOf(currentRow.getPos()));
                break;
            case "Buddy":
                currentRow.setBuddy(value.isEmpty() ? "0" : value);
                break;
            case "Inverts":
                currentRow.setInverts(value.isEmpty() ? "0" : value);
                break;
            case "Diver":
                currentRow.setDiver(value);
                break;
            case "Site No.":
                currentRow.setSiteCode(value);
                break;
            case "Site Name":
                currentRow.setSiteName(value);
                break;
            case "Latitude":
                currentRow.setLatitude(value);
                break;
            case "Longitude":
                currentRow.setLongitude(value);
                break;
            case "Date":
                currentRow.setDate(value);
                break;
            case "vis":
                currentRow.setVis(value);
                break;
            case "Direction":
                currentRow.setDirection(value);
                break;
            case "Time":
                currentRow.setTime(value);
                break;
            case "P-Qs":
                currentRow.setPqs(value);
                break;
            case "Depth":
                currentRow.setDepth(value);
                break;
            case "Method":
                currentRow.setMethod(value);
                break;
            case "Block":
                currentRow.setBlock(value);
                break;
            case "Code":
                currentRow.setCode(value);
                break;
            case "Species":
                currentRow.setSpecies(value);
                break;
            case "Common name":
                currentRow.setCommonName(value);
                break;
            case "Total":
                currentRow.setTotal(value);
                break;
            case "Use InvertSizing":
                currentRow.setIsInvertSizing(value);
                break;
            default:
                if (value.length() > 0 && requiredHeaders.contains(columnHeader)
                        && columnHeader.matches("\\d.*"))
                    measureJson.put(requiredHeaders.indexOf(columnHeader) - requiredHeaders.indexOf("Inverts"),
                            value);
                break;
        }
    }

    @Value
    public class ParsedSheet {
        private List<StagedRow> stagedRows;
    }
}