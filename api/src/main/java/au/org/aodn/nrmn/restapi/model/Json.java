package au.org.aodn.nrmn.restapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Json implements Serializable {

    private String MPA;

    private String[] OldSiteCodes;

    @JsonProperty("MPA")
    public String getMPA() {
        return MPA;
    }

    @JsonProperty("MPA")
    public void setMPA(String MPA) {
        this.MPA = MPA;
    }

    @JsonProperty("OldSiteCodes")
    public String[] getOldSiteCodes() {
        return OldSiteCodes;
    }

    @JsonProperty("OldSiteCodes")
    public void setOldSiteCodes(String[] OldSiteCodes) {
        this.OldSiteCodes = OldSiteCodes;
    }
}
