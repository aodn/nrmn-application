package au.org.aodn.nrmn.restapi.dto.payload;

public class JwtAuthenticationResponse {

    private String gridLicence;
    private String tokenType = "Bearer";

    private String accessToken;
    private String[] features;
    private Boolean changePassword;

    public JwtAuthenticationResponse() {
        this.changePassword = true;
    }

    public JwtAuthenticationResponse(String accessToken, String gridLicence, String[] features) {
        this.accessToken = accessToken;
        this.gridLicence = gridLicence;
        this.features = features;
        this.changePassword = false;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public String getGridLicense() {
        return this.gridLicence;
    }

    public String[] getFeatures() {
        return this.features;
    }

    public Boolean getChangePassword() {
        return this.changePassword;
    }
}
