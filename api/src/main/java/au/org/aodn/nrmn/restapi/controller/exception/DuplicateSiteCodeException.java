package au.org.aodn.nrmn.restapi.controller.exception;

public class DuplicateSiteCodeException extends ValidationException {
    public DuplicateSiteCodeException() {
        super("Site", "siteCode", "A site with this code already exists.");
    }
}
