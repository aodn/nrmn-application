package au.org.aodn.nrmn.restapi.service;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVFilterPrinter {

    private File file;
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;

    private String filterValue;
    private Integer filterColumn;

    private static final Logger logger = LoggerFactory.getLogger(CSVFilterPrinter.class);

    public CSVFilterPrinter(List<String> headers, String viewName, Integer filterColumn, String filterValue) {
        this.filterColumn = filterColumn;
        this.filterValue = filterValue;
        try {
            file = File.createTempFile(viewName, ".csv");
            fileWriter = new FileWriter(file);
            var headerFormat = CSVFormat.Builder.create().setHeader(headers.toArray(new String[0])).build();
            csvPrinter = new CSVPrinter(fileWriter, headerFormat);
        } catch (Exception e) {
            logger.error(viewName + " " + filterValue + " " + e.getMessage());
        }
    }

    public void writeOut(List<Object[]> inValues) {

        var values = List.copyOf(inValues);
        if (filterColumn != null && filterColumn >= 0 && filterValue != null)
            values = values.stream().filter(v -> v[filterColumn] != null && ((String) v[filterColumn]).equalsIgnoreCase(filterValue)).collect(Collectors.toList());

        try {
            csvPrinter.printRecords(values);
        } catch (Exception e) {
            logger.error(filterValue + " " + e.getMessage());
        }
    }

    public File getFile() {
        return file;
    }

    public void close() {
        try {
            csvPrinter.close();
            fileWriter.close();
            // file.delete();
        } catch (Exception e) {
            logger.error(filterValue + " " + e.getMessage());
        }
    }
}
