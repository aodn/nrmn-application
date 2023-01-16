package au.org.aodn.nrmn.restapi.service;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVFilterPrinter {

    private File file;
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;
    private String viewName;
    private String filterValue;
    private Integer filterColumn;

    private static final Logger logger = LoggerFactory.getLogger(CSVFilterPrinter.class);

    public File getFile() {
        return file;
    }

    public String getViewName() {
        return viewName;
    }

    public CSVFilterPrinter(List<String> headers, String viewName, Integer filterColumn, String filterValue) {
        this.viewName = viewName;
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

    public void writeOut(List<Object[]> values) {
        var filter = (filterColumn != null && filterColumn >= 0 && filterValue != null);
        try {
            var outValues = values.stream().filter(r -> !filter || ((String)r[filterColumn]).equalsIgnoreCase(filterValue)).toArray();
            csvPrinter.printRecords(outValues);
            csvPrinter.flush();
        } catch (Exception e) {
            logger.error(filterValue + " " + e.getMessage());
        }
    }

    public void close() {
        try {
            csvPrinter.close(true);
            fileWriter.close();
            file.delete();
        } catch (Exception e) {
            logger.error(filterValue + " " + e.getMessage());
        }
    }
}
