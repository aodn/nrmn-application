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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import au.org.aodn.nrmn.restapi.service.SurveyContentsHandler.ParsedSheet;

@Service
public class SpreadSheetService {

    @Value("${app.excel.header1.short}")
    private List<String> header1Short;

    @Value("${app.excel.header1.short.ignore}")
    private List<String> header1ShortIgnore;

    @Value("${app.excel.header1.long}")
    private List<String> header1Long;

    @Value("${app.excel.header1.long.ignore}")
    private List<String> header1LongIgnore;

    public ParsedSheet stageXlsxFile(MultipartFile file,
            Boolean withExtendedSizes) throws Exception {

        ZipSecureFile.setMinInflateRatio(0.0d);

        try (InputStream inputStream = file.getInputStream()) {

            OPCPackage opcPackage = OPCPackage.open(inputStream);
            XSSFReader xssfReader = new XSSFReader(opcPackage);

            SurveyContentsHandler surveyContentsHandler = new SurveyContentsHandler(
                    (withExtendedSizes) ? header1Long : header1Short,
                    (withExtendedSizes) ? header1LongIgnore : header1ShortIgnore);

            StylesTable styles = xssfReader.getStylesTable();

            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(opcPackage);

            SurveyCellFormatter surveyCellFormatter = new SurveyCellFormatter();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, sharedStrings, surveyContentsHandler,
                    surveyCellFormatter, false);

            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(handler);

            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            while ((sheets instanceof SheetIterator) && sheets.hasNext()) {
                try (InputStream i = sheets.next()) {
                    if (((SheetIterator) (sheets)).getSheetName().toUpperCase().contentEquals("DATA")) {
                        parser.parse(new InputSource(i));
                    }
                }
            }
            if(surveyContentsHandler.getError() != null)
                throw new Exception(surveyContentsHandler.getError());
            return surveyContentsHandler.getResult();
        } catch (NotOfficeXmlFileException e) {
            throw new Exception("Does not appear to be an XLSX Excel file. Please open this file in Excel and save as Excel Workbook (*.xlsx)");
        } catch (POIXMLException e) {
            throw new Exception("This document type is not supported. Please open this file in Excel and save as Excel Workbook (*.xlsx)");
        }
    }
}
