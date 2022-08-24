package au.org.aodn.nrmn.restapi.repository.projections;

import au.org.aodn.nrmn.restapi.model.db.Location;

import java.util.stream.Collectors;

public class LocationExtendedMapping {

    private int id;
    private String locationName;
    private String countries;
    private String areas;
    private String ecoRegions;
    private String status;
    private String siteCodes;

    public LocationExtendedMapping(Location location) {
        this.id = location.getLocationId();
        this.locationName = location.getLocationName();
        this.countries = location.getSite().stream().map(n -> n.getCountry()).sorted().collect(Collectors.joining(","));
        this.areas = location.getSite().stream().map(n -> n.getState()).sorted().collect(Collectors.joining(","));
        this.ecoRegions = location.getMeowRegions().stream().map(n -> n.getEcoRegion()).sorted().collect(Collectors.joining(","));
        this.status = location.getIsActive() ? "Active" : "Inactive";
        this.siteCodes = location.getSite().stream().map(n -> n.getSiteCode()).sorted().collect(Collectors.joining(","));;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas;
    }

    public String getEcoRegions() {
        return ecoRegions;
    }

    public void setEcoRegions(String ecoRegions) {
        this.ecoRegions = ecoRegions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(String siteCodes) {
        this.siteCodes = siteCodes;
    }
}
