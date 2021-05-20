package au.org.aodn.nrmn.restapi.controller.exception;

public class DuplicateSiteNameException extends ValidationException {
    public DuplicateSiteNameException() {
        super("Site", "siteName", "A site with that name already exists in that location.");
    }
}
