package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.Value;

public class SurveyContentsHandler implements SheetContentsHandler {

    private final List<String> header1Required;
    private final List<String> header1Optional;

    private String error;
    private ParsedSheet result;

    private StagedRow currentRow;
    private boolean isHeaderRow = false;
    private boolean isHeader2Row = false;

    private HashMap<String, String> header1 = new HashMap<>();
    private HashMap<String, String> header2 = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<>();

    List<StagedRow> stagedRows = new ArrayList<>();
    List<String> columnKeys = new ArrayList<>();

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        this.header1Required = requiredHeaders;
        this.header1Optional = optionalHeaders;
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
        isHeader2Row = (rowNum == 1);
        if (!isHeaderRow && !isHeader2Row) {
            currentRow = StagedRow.builder().pos((rowNum - 1) * 1000).build();
            for (String col : header1Required)
                setValue(col, "");
        }
    }

    @Override
    public void endRow(int rowNum) {
        if (isHeaderRow) {
            List<String> errors = new ArrayList<String>();
            List<String> foundHeaders = new ArrayList<String>(header1.values());
            List<String> missingHeaders = new ArrayList<String>(header1Required);
            missingHeaders.removeAll(foundHeaders);
            if (missingHeaders.size() > 0)
                errors.add("Row 1 missing headers: " + String.join(", ", missingHeaders));
            foundHeaders.removeAll(header1Required);
            foundHeaders.removeAll(header1Optional);
            if (foundHeaders.size() > 0)
                errors.add("Row 1 has unexpected headers: " + String.join(", ", foundHeaders));
            if (errors.size() > 0) {
                error = String.join(". ", errors);
            } else {
                String[] headers = header1.keySet().toArray(new String[0]);
                Arrays.sort(headers, (str1, str2) -> str1.length() - str2.length());
                columnKeys = Arrays.asList(headers);
            }
        } else if (isHeader2Row && columnKeys.size() > 0) {
            // Check that the first 10 columns are blank
            Long invalidColumns = columnKeys.subList(0, 10).stream().filter(k -> header2.get(k) != null).count();
            if (invalidColumns > 0)
                error = "Cell range A2-G2 is not blank.";
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
            header1.put(columnKey, formattedValue);
        } else if (isHeader2Row) {
            header2.put(columnKey, formattedValue);
        } else {
            String col = header1.getOrDefault(columnKey, "");
            boolean cellHasData = StringUtils.isNotEmpty(formattedValue) && !formattedValue.contentEquals("null");
            String value = cellHasData ? formattedValue : "";
            setValue(col, value);
        }
    }

    private String truncateDecimalString(String decimalString) {
        int idx = decimalString.indexOf('.');
        int endIdx = idx + 6;
        if (idx >= 0 && endIdx < decimalString.length())
            return decimalString.substring(0, endIdx);
        else
            return decimalString;
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
                currentRow.setLatitude(truncateDecimalString(value));
                break;
            case "Longitude":
                currentRow.setLongitude(truncateDecimalString(value));
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
                if (value.length() > 0 && header1Required.contains(columnHeader)
                        && columnHeader.matches("\\d.*"))
                    measureJson.put(header1Required.indexOf(columnHeader) - header1Required.indexOf("Inverts"),
                            value);
                break;
        }
    }

    @Value
    public class ParsedSheet {
        private List<StagedRow> stagedRows;
    }
}