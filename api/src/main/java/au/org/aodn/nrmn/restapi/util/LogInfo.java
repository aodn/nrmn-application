package au.org.aodn.nrmn.restapi.util;

import org.apache.logging.log4j.ThreadContext;

public class LogInfo {

    public static String withContext(String message) {
        return String.format("ID: %s Username: %s Message: %s",
                ThreadContext.get("requestId"),
                ThreadContext.get("username"),
                message);
    }
}
