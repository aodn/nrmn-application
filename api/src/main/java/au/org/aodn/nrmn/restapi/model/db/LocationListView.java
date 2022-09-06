package au.org.aodn.nrmn.restapi.model.db;

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
 * This view is use by the GUI location list screen
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        "SELECT distinct loc.location_id as id, loc.location_name as locationName, " +
        "CASE loc.is_active WHEN true THEN 'Active' ELSE 'Inactive' END as status, " +
        "string_agg(DISTINCT sit.country, ', ' ORDER BY sit.country) AS countries, "+
        "string_agg(DISTINCT sit.state, ', ' ORDER BY sit.state) AS areas, "+
        "string_agg(DISTINCT sit.site_code, ', ' ORDER BY sit.site_code) AS siteCodes, " +
        "string_agg(DISTINCT meo.ecoregion, ', ' ORDER BY meo.ecoregion) AS eco_regions "+
        "FROM nrmn.location_ref loc " +
        "LEFT JOIN nrmn.site_ref sit ON loc.location_id = sit.location_id " +
        "LEFT JOIN nrmn.meow_ecoregions meo ON st_contains(meo.geom, sit.geom) " +
        "LEFT JOIN nrmn.survey sur ON sur.site_id = sit.site_id " +
        "GROUP BY loc.location_id, locationName"
)
public class LocationListView {

    @Id
    private int id;

    @Column(name="locationname")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String locationName;

    @Column(name="status")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String status;

    @Column(name="countries")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String countries;

    @Column(name="areas")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String areas;

    @Column(name="sitecodes")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String siteCodes;

    @Column(name="eco_regions")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String ecoRegions;

    @JsonGetter
    public int getId() {
        return id;
    }

    @JsonGetter
    public String getLocationName() {
        return locationName;
    }

    @JsonGetter
    public String getStatus() {
        return status;
    }

    @JsonGetter
    public String getCountries() {
        return countries;
    }

    @JsonGetter
    public String getAreas() {
        return areas;
    }

    @JsonGetter
    public String getSiteCodes() {
        return siteCodes;
    }

    @JsonGetter
    public String getEcoRegions() {
        return ecoRegions;
    }
}
