package au.org.aodn.nrmn.restapi.util;

import java.util.Arrays;
import java.util.List;

public class MeasureUtil {
    static final private List<String> fishMeasureNames = Arrays.asList(
            "2.5", "5", "7.5", "10", "12.5", "15", "20", "25",
            "30", "35", "40", "50", "62.5", "75", "87.5", "100",
            "112.5", "125", "137.5", "150", "162.5", "175", "187.5",
            "200", "250", "300", "350", "400", "450", "500", "550",
            "600", "650", "700", "750", "800", "850", "900", "950", "1000");

            static final private List<String> invertMeasureNames = Arrays.asList(
                "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", 
                "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9", "9.5", "10",
                 "10.5", "11", "11.5", "12", "12.5", "13", "13.5", "14", "14.5", 
                 "15", "16", "17", "18", "19", "20", "22", "24", "26", "28", "30");

    static public String getMeasureName(Integer pos, boolean invertSizing) {
        List<String> names = invertSizing ? invertMeasureNames : fishMeasureNames;
        if (pos > names.size())
            return "";
        return names.get(pos-1);
    }
}
