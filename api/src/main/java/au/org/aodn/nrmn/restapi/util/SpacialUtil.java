package au.org.aodn.nrmn.restapi.util;

public class SpacialUtil {

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            // 60 * 1.1515 * 1.609344;
            return Math.toDegrees(Math.acos(dist)) * 111.18957696;
        }
    }

}
