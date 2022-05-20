package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import lombok.Value;

public class SurveyContentsHandler implements SheetContentsHandler {

    private final List<Field> header1Required;
    private final List<Field> header1Optional;

    private String error;
    private ParsedSheet result;

    private StagedRow currentRow;
    private boolean isHeaderRow = false;
    private boolean isHeader2Row = false;

    private HashMap<String, Field> header1 = new HashMap<>();
    private HashMap<String, Field> header2 = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<>();

    List<StagedRow> stagedRows = new ArrayList<>();
    List<String> columnKeys = new ArrayList<>();

    public enum Field {
        UNKNOWN("UNKNOWN"),
        ID("ID"), BUDDY("Buddy"), INVERTS("Inverts"), DIVER("Diver"),
        SITE_NO("Site No."), SITE_NAME("Site Name"), LATITUDE("Latitude"), LONGITUDE("Longitude"),
        DATE("Date"), VIS("Vis"), DIRECTION("Direction"), TIME("Time"), P_QS("P-Qs"), DEPTH("Depth"),
        METHOD("Method"), BLOCK("Block"), CODE("Code"), SPECIES("Species"), COMMON_NAME("Common Name"),
        TOTAL("Total"), USE_INVERT_SIZING("Use InvertSizing"), M2_INVERT_SIZING_SPECIES("M2 Invert Sizing Species"),
        L5("L5"), L95("L95"), LMAX("Lmax"),
        TWO_FIVE("2.5", Boolean.TRUE, 1), FIVE("5", Boolean.TRUE, 2), SEVEN_FIVE("7.5", Boolean.TRUE, 3),
        TEN("10", Boolean.TRUE, 4), TWELVE_FIVE("12.5", Boolean.TRUE, 5), FIFTEEN("15", Boolean.TRUE, 6),
        TWENTY("20", Boolean.TRUE, 7), TWENTY_FIVE("25", Boolean.TRUE, 8), THIRTY("30", Boolean.TRUE, 9),
        THIRTY_FIVE("35", Boolean.TRUE, 10), FORTY("40", Boolean.TRUE, 11), FIFTY("50", Boolean.TRUE, 12),
        SIXTY_TWO_FIVE("62.5", Boolean.TRUE,13), SEVENTY_FIVE("75", Boolean.TRUE, 14),
        EIGHTY_SEVEN_FIVE("87.5", Boolean.TRUE, 15), HUNDRED("100", Boolean.TRUE, 16),
        HUNDRED_TWELVE_FIVE("112.5", Boolean.TRUE, 17), HUNDRED_TWENTY_FIVE("125", Boolean.TRUE, 18),
        HUNDRED_THIRTY_SEVEN_FIVE("137.5", Boolean.TRUE, 19), HUNDRED_FIFTY("150", Boolean.TRUE, 20),
        HUNDRED_SIXTY_TWO_FIVE("162.5", Boolean.TRUE, 21), HUNDRED_SEVENTY_FIVE("175", Boolean.TRUE, 22),
        HUNDRED_EIGHTY_SEVEN_FIVE("187.5", Boolean.TRUE, 23), TWO_HUNDRED("200", Boolean.TRUE, 24),
        TWO_HUNDRED_FIFTY("250", Boolean.TRUE, 25), THREE_HUNDRED("300", Boolean.TRUE, 26),
        THREE_HUNDRED_FIFTY("350", Boolean.TRUE, 27), FOUR_HUNDRED("400", Boolean.TRUE, 28),
        FOUR_HUNDRED_FIFTY("450", Boolean.TRUE, 29), FIVE_HUNDRED("500", Boolean.TRUE, 30),
        FIFE_HUNDRED_FIFTY("550", Boolean.TRUE, 31), SIX_HUNDRED("600", Boolean.TRUE, 32),
        SIX_HUNDRED_FIFTY("650", Boolean.TRUE, 33), SEVEN_HUNDRED("700", Boolean.TRUE, 34),
        SEVEN_HUNDRED_FIFTY("750", Boolean.TRUE, 35), EIGHT_HUNDRED("800", Boolean.TRUE, 36),
        EIGHT_HUNDRED_FIFTY("850", Boolean.TRUE, 37), NINE_HUNDRED("900", Boolean.TRUE, 38),
        NINE_HUNDRED_FIFTY("950", Boolean.TRUE, 39), THOUSAND("1000", Boolean.TRUE, 40);


        private final String val;
        private final Boolean measurement;
        private final Integer pos;

        Field(String v) {
            this(v, Boolean.FALSE, -1);
        }

        Field(String v, Boolean measurement, Integer pos) {
            this.val = v;
            this.measurement = measurement;
            this.pos = pos;
        }

        public static Field getEnum(final String v) {
            return Arrays.stream(Field.values())
                    // Avoid UI export and excel header cases sensitive issue
                    .filter(p -> v != null && p.val.equalsIgnoreCase(v.trim()))
                    .findFirst()
                    .orElse(UNKNOWN);
        }

        public Boolean isMeasurement() {
            return measurement;
        }

        public Integer getPosition() {
            return pos;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        // Make sure header values set is correct and known, otherwise it will result in missing fields value
        this.header1Required = new ArrayList<>();
        for(String s: requiredHeaders) {
            Field f = Field.getEnum(s);
            assert f != Field.UNKNOWN : "Require field name " + s + " is not defined";
            this.header1Required.add(f);
        }

        this.header1Optional = new ArrayList<>();
        for(String s: optionalHeaders) {
            Field f = Field.getEnum(s);
            assert f != Field.UNKNOWN : "Optional field name " + s + " is not defined";
            this.header1Optional.add(f);
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
            for (Field col : header1Required)
                setValue(col, "");
        }
    }

    @Override
    public void endRow(int rowNum) {
        if (isHeaderRow) {
            List<String> errors = new ArrayList<>();
            List<Field> foundHeaders = new ArrayList<>(header1.values());
            List<Field> missingHeaders = new ArrayList<>(header1Required);
            missingHeaders.removeAll(foundHeaders);
            if (missingHeaders.size() > 0)
                errors.add("Row 1 missing headers: " + String.join(", ", missingHeaders.stream().map(s -> s.toString()).collect(Collectors.toList())));
            foundHeaders.removeAll(header1Required);
            foundHeaders.removeAll(header1Optional);
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
            header1.put(columnKey, Field.getEnum(formattedValue));
        } else if (isHeader2Row) {
            header2.put(columnKey, Field.getEnum(formattedValue));
        } else {
            Field col = header1.getOrDefault(columnKey, Field.UNKNOWN);
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

    private void setValue(Field columnHeader, String formattedValue) {
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
                if (value.length() > 0 && header1Required.contains(columnHeader) && columnHeader.isMeasurement()) {
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