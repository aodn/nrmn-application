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

    public enum Fields {
        UNKNOWN("UNKNOWN"),
        ID("ID"), BUDDY("Buddy"), INVERTS("Inverts"), DIVER("Diver"), SITE_CODE("Site Code"),
        SITE_NO("Site No."), SITE_NAME("Site Name"), LATITUDE("Latitude"), LONGITUDE("Longitude"),
        DATE("Date"), VIS("Vis"), DIRECTION("Direction"), TIME("Time"), P_QS("P-Qs"), DEPTH("Depth"),
        METHOD("Method"), BLOCK("Block"), CODE("Code"), SPECIES("Species"), COMMON_NAME("Common Name"),
        TOTAL("Total"), USE_INVERT_SIZING("Use InvertSizing"), TWO_FIVE("2.5"), FIVE("5"),
        SEVEN_FIVE("7.5"), TEN("10"), TWELVE_FIVE("12.5"), FIFTEEN("15"), TWENTY("20"),
        TWENTY_FIVE("25"), THIRTY("30"), THIRTY_FIVE("35"), FORTY("40"), FIFTY("50"),
        SIXTY_TWO_FIVE("62.5"), SEVENTY_FIVE("75"), EIGHTY_SEVEN_FIVE("87.5"), HUNDRED("100"),
        HUNDRED_TWELVE_FIVE("112.5"), HUNDRED_TWENTY_FIVE("125"), HUNDRED_THIRTY_SEVEN_FIVE("137.5"),
        HUNDRED_FIFTY("150"), HUNDRED_SIXTY_TWO_FIVE("162.5"), HUNDRED_SEVENTY_FIVE("175"),
        HUNDRED_EIGHTY_SEVEN_FIVE("187.5"), TWO_HUNDRED("200"), TWO_HUNDRED_FIFTY("250"), THREE_HUNDRED("300"),
        THREE_HUNDRED_FIFTY("350"), FOUR_HUNDRED("400"), FOUR_HUNDRED_FIFTY("450"), FIVE_HUNDRED("500"),
        FIFE_HUNDRED_FIFTY("550"), SIX_HUNDRED("600"), SIX_HUNDRED_FIFTY("650"), SEVEN_HUNDRED("700"),
        SEVEN_HUNDRED_FIFTY("750"), EIGHT_HUNDRED("800"), EIGHT_HUNDRED_FIFTY("850"), NINE_HUNDRED("900"),
        NINE_HUNDRED_FIFTY("950"), THOUSAND("1000"), M2_INVERT_SIZING_SPECIES("M2 Invert Sizing Species"),
        L5("L5"), L95("L95"), LMAX("Lmax");

        private final String val;

        Fields(String v) {
            this.val = v;
        }

        public static Fields getEnum(String v) {
            return Arrays.stream(Fields.values())
                    // Avoid UI export and excel header cases diff
                    .filter(p -> p.val.equalsIgnoreCase(v))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        // Make sure header values set is correct and known, otherwise it will result in missing fields value
        for(String s: requiredHeaders) {
            assert Fields.getEnum(s) != Fields.UNKNOWN : "Field name " + s + " is not defined";
        }

        for(String s: optionalHeaders) {
            assert Fields.getEnum(s) != Fields.UNKNOWN : "Field name " + s + " is not defined";
        }

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
        switch (Fields.getEnum(columnHeader)) {
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
            case SITE_CODE:
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