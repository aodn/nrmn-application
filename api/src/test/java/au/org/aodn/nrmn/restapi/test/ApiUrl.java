package au.org.aodn.nrmn.restapi.test;

public class ApiUrl {
    public static String entityRef(int port, String entityPath, Number id) {
        return String.format("http://localhost:%d/api/%s/%d", port, entityPath, id);
    }
}
