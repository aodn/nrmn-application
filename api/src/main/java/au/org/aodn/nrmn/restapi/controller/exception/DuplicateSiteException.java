package au.org.aodn.nrmn.restapi.controller.exception;

public class DuplicateSiteException extends ValidationException {
    public DuplicateSiteException() {
        super("SiteDto", "siteName", "A site with that code and name already exists.");
    }
}
