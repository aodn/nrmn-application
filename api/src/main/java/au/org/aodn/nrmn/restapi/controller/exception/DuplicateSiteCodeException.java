package au.org.aodn.nrmn.restapi.controller.exception;

public class DuplicateSiteCodeException extends ValidationException {
    public DuplicateSiteCodeException() {
        super("Site", "siteName", "A site with that code already exists.");
    }
}
