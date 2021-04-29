package au.org.aodn.nrmn.restapi.service;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import cyclops.control.Future;
import cyclops.control.Validated;

@Service
public class SpreadSheetService {

    @Value("${app.excel.headers.short}")
    private List<String> shortHeadersRef;

    @Value("${app.excel.headers.long}")
    private List<String> longHeadersRef;

    @Autowired
    private S3IO s3client;
    
    public Validated<ErrorInput, List<StagedRow>> stageXlsxFile(MultipartFile file, Boolean withInvertSize) {
        try {
            InputStream inputStream = file.getInputStream();
            OPCPackage opcPackage = OPCPackage.open(inputStream);
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            ZipSecureFile.setMinInflateRatio(0.0d);

            SurveyContentsHandler surveyContentsHandler = new SurveyContentsHandler((withInvertSize) ? longHeadersRef : shortHeadersRef);

            StylesTable styles = xssfReader.getStylesTable();

            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(opcPackage);

            ContentHandler handler = new XSSFSheetXMLHandler(styles, sharedStrings, surveyContentsHandler, false);

            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while ((sheets instanceof SheetIterator) && sheets.hasNext()) {
                InputStream i = sheets.next();
                try {
                    if (((SheetIterator) (sheets)).getSheetName().toUpperCase().contentEquals("DATA")) {
                        parser.parse(new InputSource(i));
                    }
                } finally {
                    i.close();
                }
            }
            return surveyContentsHandler.getResult();
        } catch (NotOfficeXmlFileException e) {
            return Validated.invalid(new ErrorInput("Does not appear to be an XLSX Excel file. Please open this file in Excel and save as Excel Workbook (*.xlsx)", "excel"));
        } catch (POIXMLException e) {
            return Validated.invalid(new ErrorInput("This document type is not supported. Please open this file in Excel and save as Excel Workbook (*.xlsx)", "excel"));
        } catch (Exception e) {
            return Validated.invalid(new ErrorInput(e.getMessage(), "excel"));
        }
    }

    public void saveToS3(MultipartFile file, Long jobId) {
        Future.of(() -> s3client.write("/raw-survey/" + file.getOriginalFilename() + "-" + jobId+ ".xlsx", file));
    }
}
