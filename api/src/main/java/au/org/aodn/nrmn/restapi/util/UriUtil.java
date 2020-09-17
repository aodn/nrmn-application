package au.org.aodn.nrmn.restapi.util;

import javax.servlet.http.HttpServletRequest;

public class UriUtil {

    public static String baseUri(HttpServletRequest request) {
        String thePort = (request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + String.valueOf(request.getServerPort());
        return String.format("%s://%s%s", request.getScheme(), request.getServerName(), thePort);
    }

    public static String createtokenUriFromBaseUri(String baseTokenUri, String page, String username, String token) {
        return String.format("%s/%s?username=%s&token=%s", baseTokenUri, page, username, token);
    }

    public static String createResourceUri(HttpServletRequest request, String page) {
        return String.format("%s/%s", baseUri(request), page);
    }

    public static String createResourceUriWithId(HttpServletRequest request, String page, Long id) {
        return String.format("%s/%s", createResourceUri(request, page), id);
    }
}
