package au.org.aodn.nrmn.restapi.service.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.enums.SurveyField;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import lombok.Value;

public class SurveyContentsHandler implements SheetContentsHandler {

    private final List<SurveyField> requiredSurveyFields;
    private final List<SurveyField> optionalSurveyFields;

    private String error;
    private ParsedSheet result;

    private StagedRow currentRow;
    private boolean isHeaderRow = false;
    private boolean isHeader2Row = false;

    private HashMap<String, SurveyField> header1 = new HashMap<>();
    private HashMap<String, SurveyField> header2 = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<>();

    List<StagedRow> stagedRows = new ArrayList<>();
    List<String> columnKeys = new ArrayList<>();

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        // Make sure header values set is correct and known, otherwise it will result in missing fields value
        this.requiredSurveyFields = new ArrayList<>();
        for(String s: requiredHeaders) {
            SurveyField f = SurveyField.getEnum(s);
            assert f != SurveyField.UNKNOWN : "Require field name " + s + " is not defined";
            this.requiredSurveyFields.add(f);
        }

        this.optionalSurveyFields = new ArrayList<>();
        for(String s: optionalHeaders) {
            SurveyField f = SurveyField.getEnum(s);
            assert f != SurveyField.UNKNOWN : "Optional field name " + s + " is not defined";
            this.optionalSurveyFields.add(f);
        }
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
            for (SurveyField col : requiredSurveyFields)
                setValue(col, "");
        }
    }

    @Override
    public void endRow(int rowNum) {
        if (isHeaderRow) {
            List<String> errors = new ArrayList<>();
            List<SurveyField> foundHeaders = new ArrayList<>(header1.values());
            List<SurveyField> missingHeaders = new ArrayList<>(requiredSurveyFields);
            missingHeaders.removeAll(foundHeaders);
            if (missingHeaders.size() > 0)
                errors.add("Row 1 missing headers: " + String.join(", ", missingHeaders.stream().map(s -> s.toString()).collect(Collectors.toList())));
            foundHeaders.removeAll(requiredSurveyFields);
            foundHeaders.removeAll(optionalSurveyFields);
            if (foundHeaders.size() > 0)
                errors.add("Row 1 has unexpected headers: " + String.join(", ", foundHeaders.stream().map(s -> s.toString()).collect(Collectors.toList())));
            if (errors.size() > 0) {
                error = String.join(". ", errors);
            } else {
                String[] headers = header1.keySet().toArray(new String[0]);
                Arrays.sort(headers, (str1, str2) -> str1.length() - str2.length());
                columnKeys = Arrays.asList(headers);
            }
        } else if (isHeader2Row && columnKeys.size() > 0) {
            // Check that the first 10 columns are blank for row 2, a requirement for Excel import file.
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
            header1.put(columnKey, SurveyField.getEnum(formattedValue));
        } else if (isHeader2Row) {
            header2.put(columnKey, SurveyField.getEnum(formattedValue));
        } else {
            SurveyField col = header1.getOrDefault(columnKey, SurveyField.UNKNOWN);
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

    private void setValue(SurveyField columnHeader, String formattedValue) {
        String value = formattedValue != null ? formattedValue.trim() : "";
        switch (columnHeader) {
            case ID:
                currentRow.setId(Long.valueOf(currentRow.getPos()));
                break;
            case BUDDY:
                currentRow.setBuddy(value.isEmpty() ? "0" : value);
                break;
            case INVERTS:
                currentRow.setInverts(value.isEmpty() ? "0" : value);
                break;
            case DIVER:
                currentRow.setDiver(value);
                break;
            case SITE_NO:
                currentRow.setSiteCode(value);
                break;
            case SITE_NAME:
                currentRow.setSiteName(value);
                break;
            case LATITUDE:
                currentRow.setLatitude(truncateDecimalString(value));
                break;
            case LONGITUDE:
                currentRow.setLongitude(truncateDecimalString(value));
                break;
            case DATE:
                currentRow.setDate(value);
                break;
            case VIS:
                currentRow.setVis(value);
                break;
            case DIRECTION:
                currentRow.setDirection(value);
                break;
            case TIME:
                currentRow.setTime(value);
                break;
            case P_QS:
                currentRow.setPqs(value);
                break;
            case DEPTH:
                currentRow.setDepth(value);
                break;
            case METHOD:
                currentRow.setMethod(value);
                break;
            case BLOCK:
                currentRow.setBlock(value);
                break;
            case CODE:
                currentRow.setCode(value);
                break;
            case SPECIES:
                currentRow.setSpecies(value);
                break;
            case COMMON_NAME:
                currentRow.setCommonName(value);
                break;
            case TOTAL:
                currentRow.setTotal(value);
                break;
            case USE_INVERT_SIZING:
                currentRow.setIsInvertSizing(value);
                break;
            case UNKNOWN:
                // Do nothing as field not known by the handler and if it is required field will be error out later.
                break;
            default:
                if (value.length() > 0 && requiredSurveyFields.contains(columnHeader) && columnHeader.isMeasurement()) {
                    measureJson.put(columnHeader.getPosition(), value);
                }
                break;
        }
    }

    @Value
    public class ParsedSheet {
        private List<StagedRow> stagedRows;
    }
}