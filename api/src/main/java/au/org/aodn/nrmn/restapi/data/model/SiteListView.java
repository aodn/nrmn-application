package au.org.aodn.nrmn.restapi.data.model;

import au.org.aodn.nrmn.restapi.enums.Iirc;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * This view is use by the GUI site screen, we do not use the Diver db entity directly because
 * we want to allow flexible table join here if needed in future
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        // Round latitude and longitude with 5 decimal for display only
        "SELECT " +
                "s.site_id, s.site_code, s.site_name, " +
                "ROUND(s.longitude::numeric, " + Iirc.ROUNDING_DIGIT + ") as longitude, " +
                "ROUND(s.latitude::numeric, " + Iirc.ROUNDING_DIGIT + ") as latitude, s.state, " +
                "s.country, loc.location_name, " +
                "(CASE WHEN s.is_active = true THEN 'true' ELSE 'false' END) AS status " +
                "FROM nrmn.site_ref s " +
                "INNER JOIN nrmn.location_ref loc ON s.location_id = loc.location_id " +
                "WHERE s.site_code IS NOT NULL " +
                "ORDER BY SUBSTRING(s.site_code, '^[A-Z]+'), CAST(SUBSTRING(s.site_code, '[0-9]+$') AS INTEGER)"
)
public class SiteListView {
    @Id
    @Column(name = "site_id")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Integer siteId;

    @Column(name = "site_code")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String siteCode;

    @Column(name = "site_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String siteName;

    @Column(name = "location_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String locationName;

    @Column(name = "state")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String state;

    @Column(name = "country")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String country;

    @Column(name = "latitude")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String latitude;

    @Column(name = "longitude")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String longitude;

    @Column(name = "status")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String isActive;

    @JsonGetter
    public Integer getSiteId() {
        return siteId;
    }

    @JsonGetter
    public String getSiteCode() {
        return siteCode;
    }

    @JsonGetter
    public String getSiteName() {
        return siteName;
    }

    @JsonGetter
    public String getLocationName() {
        return locationName;
    }

    @JsonGetter
    public String getState() {
        return state;
    }

    @JsonGetter
    public String getCountry() {
        return country;
    }

    @JsonGetter
    public String getLatitude() {
        return latitude;
    }

    @JsonGetter
    public String getLongitude() {
        return longitude;
    }

    @JsonGetter
    public String getIsActive() {
        return isActive;
    }
}
