package au.org.aodn.nrmn.restapi.util;

public class SpacialUtil {

    // Haversine formula. See: http://www.movable-type.co.uk/scripts/latlong.html
    public static double getDistanceLatLongMeters(Double latitude1, Double longitude1, Double latitude2, Double longitude2) {

        try
        {
            double lat1 = latitude1;
            double lat2 = latitude2;
            double lon1 = longitude1;
            double lon2 = longitude2;

            double earthRadius = 6371000; //meters
            double dLat = Math.toRadians(lat2-lat1);
            double dLon = Math.toRadians(lon2-lon1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = earthRadius * c;
            return dist;

        } catch(NullPointerException ex) {
            return 0;
        }
    }
}
