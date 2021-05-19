package au.org.aodn.nrmn.restapi.util;

import java.util.Arrays;
import java.util.List;

public class MeasureUtil {
    static private List<String> measureNames = Arrays.asList(
            "2-5", "5", "7-5", "10", "12-5", "15", "20", "25",
            "30", "35", "40", "50", "62-5", "75", "87-5", "100",
            "112-5", "125", "137-5", "150", "162-5", "175", "187-5",
            "200", "250", "300", "350", "400", "450", "500", "550",
            "600", "650", "700", "750", "800", "850", "900", "950", "1000");

    static public String getMeasureName(Integer pos) {
        if (pos > measureNames.size())
            return "";
        return measureNames.get(pos);
    }
}
