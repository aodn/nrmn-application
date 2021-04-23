package au.org.aodn.nrmn.restapi.controller.exception;

public class SiteLocationNotFoundException extends ValidationException {
    public SiteLocationNotFoundException() {
        super("SiteDto", "locationId", "A location with that id cannot be found.");
    }
}
