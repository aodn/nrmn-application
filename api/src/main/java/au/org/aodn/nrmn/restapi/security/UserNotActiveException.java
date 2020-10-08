package au.org.aodn.nrmn.restapi.security;

public class UserNotActiveException extends RuntimeException {
    private String message;

    public UserNotActiveException(String message) {
        super(message);
    }
}