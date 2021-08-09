package au.org.aodn.nrmn.restapi.dto.payload;

public class JwtAuthenticationResponse {
    
    private String gridLicence;
    private String tokenType = "Bearer";

    private String accessToken;

    public JwtAuthenticationResponse(){}
    
    public JwtAuthenticationResponse(String accessToken, String gridLicence) {
        this.accessToken = accessToken;
        this.gridLicence = gridLicence;
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

}
