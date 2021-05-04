package au.org.aodn.nrmn.restapi.service;

import org.apache.poi.ss.usermodel.DataFormatter;

public class SurveyCellFormatter extends DataFormatter {

    @Override
    public String formatRawCellContents(double value, int formatIndex, java.lang.String formatString) {

        // Never format a date in the short US date format, even when asked.
        // http://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/BuiltinFormats.html

        if (formatIndex == 14)
            return super.formatRawCellContents(value, 165, "d/mm/yyyy;@");
        return super.formatRawCellContents(value, formatIndex, formatString);
    }
}
