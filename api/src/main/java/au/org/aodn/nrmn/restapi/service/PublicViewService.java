package au.org.aodn.nrmn.restapi.service;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import au.org.aodn.nrmn.restapi.service.upload.S3IO;

@Service
public class PublicViewService {

    private static final Logger logger = LoggerFactory.getLogger(PublicViewService.class);

    // fetch rows in batches
    private static final int FETCH_SIZE = 50000;

    private static final List<String> NRMN_PUBLIC_ENDPOINTS = List.of(
            "ep_m0_off_transect_sighting_public",
            "ep_m1_public",
            "ep_m2_cryptic_fish_public",
            "ep_m2_inverts_public",
            "ep_m3_isq_public",
            "ep_site_list_public",
            "ep_survey_list_public");

    @Value("${app.public-views.schema:nrmn}")
    private String sourceSchema;

    @Autowired
    private S3IO s3IO;

    @Autowired
    private DataSource dataSource;

    public void publishPublicViews() {
        logger.info("Publishing public views to imos-data");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (var relation : NRMN_PUBLIC_ENDPOINTS) {
            try {
                // as requested, the file naming follows "relation_data.csv" such as "ep_m0_off_transect_sighting_public_data.csv"
                publishView(relation, relation + "_data");
            } catch (Exception e) {
                logger.error("Failed to publish public view " + relation, e);
            }
        }
        stopWatch.stop();
        logger.info("Published all public views in " + stopWatch.getLastTaskTimeMillis() + "ms");
    }

    private void publishView(String relation, String outputName) throws Exception {
        logger.info("Extracting public view " + relation);
        File file = File.createTempFile(outputName, ".csv");
        var sql = "SELECT * FROM " + sourceSchema + "." + relation;
        try (Connection connection = dataSource.getConnection()) {
            // Postgres only streams a ResultSet via a server-side cursor when autoCommit is
            // off and a fetch size is set; otherwise it loads every row into memory.
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.setFetchSize(FETCH_SIZE);
                try (ResultSet rs = statement.executeQuery(sql);
                        FileWriter writer = new FileWriter(file);
                        CSVPrinter csvPrinter = new CSVPrinter(writer, buildCsvFormat(rs.getMetaData()))) {

                    var columnCount = rs.getMetaData().getColumnCount();
                    var row = new Object[columnCount];
                    while (rs.next()) {
                        for (var i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        csvPrinter.printRecord(row);
                    }
                    csvPrinter.flush();
                }
            }
            connection.commit();
            logger.info("Uploading public CSV " + outputName);
            s3IO.uploadPublicView(outputName, file);
        } finally {
            file.delete();
        }
    }

    private CSVFormat buildCsvFormat(ResultSetMetaData metaData) throws Exception {
        var headers = new String[metaData.getColumnCount()];
        for (var i = 0; i < headers.length; i++) {
            headers[i] = metaData.getColumnLabel(i + 1);
        }
        return CSVFormat.Builder.create().setHeader(headers).build();
    }
}
