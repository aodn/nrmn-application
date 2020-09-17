package au.org.aodn.nrmn.restapi.exception;

public class ValidationException extends RuntimeException {
    private String message;

    public ValidationException(String message) {
        super(message);
    }
}
