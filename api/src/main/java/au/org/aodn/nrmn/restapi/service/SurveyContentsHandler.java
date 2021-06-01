package au.org.aodn.nrmn.restapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Validated;

public class SurveyContentsHandler implements SheetContentsHandler {

    private final List<String> optionalHeaders;
    private List<String> requiredHeaders;

    private Validated<ErrorInput, List<StagedRow>> result;
    private StagedRow currentRow;
    private boolean isHeaderRow = false;
    private boolean rowHasId = false;
    private HashMap<String, String> columnHeaders = new HashMap<>();
    HashMap<Integer, String> measureJson = new HashMap<Integer, String>();
    List<StagedRow> stagedRows = new ArrayList<StagedRow>();

    SurveyContentsHandler(List<String> requiredHeaders, List<String> optionalHeaders) {
        this.requiredHeaders = requiredHeaders;
        this.optionalHeaders = optionalHeaders;
    }

    public Validated<ErrorInput, List<StagedRow>> getResult() {
        if (this.result != null)
            return this.result;
        else
            return Validated.invalid(new ErrorInput("DATA sheet not found", "excel"));
    }

    @Override
    public void startRow(int rowNum) {
        rowHasId = false;
        isHeaderRow = (rowNum == 0);
        if (!isHeaderRow) {
            currentRow = StagedRow.builder().pos(rowNum - 1).build();
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
                result = Validated.invalid(new ErrorInput(String.join(". ", errors), "headers"));
        } else {
            if (rowHasId) {
                currentRow.setMeasureJson(new HashMap<Integer, String>(measureJson));
                this.stagedRows.add(currentRow);
            }
        }
        measureJson.clear();
    }

    @Override
    public void endSheet() {
        if (stagedRows.size() > 0)
            result = Validated.valid(stagedRows);
        else
            result = result != null ? result : Validated.invalid(new ErrorInput("Empty DATA sheet", "sheet"));
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {

        if (result != null)
            return;

        String columnKey = cellReference.replaceAll("[0123456789]", "");
        if (isHeaderRow) {
            columnHeaders.put(columnKey, formattedValue);
        } else {
            String columnValue = columnHeaders.getOrDefault(columnKey, "");
            columnValue = columnValue == null || columnValue.contentEquals("") || columnValue.contentEquals("null")
                    ? "0"
                    : columnValue;
            setValue(columnValue, formattedValue);
        }
    }

    private void setValue(String columnValue, String formattedValue) {
        switch (columnValue) {
            case "ID":
                rowHasId = formattedValue.length() > 0;
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
                if (formattedValue.length() > 0 && requiredHeaders.contains(columnValue)
                        && columnValue.matches("\\d.*"))
                    measureJson.put(requiredHeaders.indexOf(columnValue) - requiredHeaders.indexOf("Inverts"),
                            formattedValue);
                break;
        }
    }
}