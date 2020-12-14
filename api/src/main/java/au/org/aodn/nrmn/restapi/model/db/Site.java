package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "site_ref")
@Audited(withModifiedFlag = true)
public class Site {
    @Id
    @SequenceGenerator(name = "site_ref_site_id", sequenceName = "site_ref_site_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_ref_site_id")
    @Schema(title = "Id", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "site_id", unique = true, updatable = false, nullable = false)
    private Integer siteId;

    @Basic
    @NotNull
    @Column(name = "site_code")
    @Schema(title = "Code")
    private String siteCode;

    @Basic
    @NotNull
    @Column(name = "site_name")
    @Schema(title = "Name")
    private String siteName;

    @Basic
    @Column(name = "longitude")
    @Schema(title = "Longitude")
    private Double longitude;

    @Basic
    @Column(name = "latitude")
    @Schema(title = "Latitude")
    private Double latitude;

    @Basic
    @Column(name = "geom")
    @Setter(AccessLevel.NONE)
    @Schema(hidden = true)
    @JsonIgnore
    private Point geom;

    @Basic
    @Column(name = "state")
    @Schema(title = "State")
    private String state;

    @Basic
    @Column(name = "country")
    @Schema(title = "Country")
    private String country;

    @Basic
    @Column(name = "old_site_code", columnDefinition = "varchar(50)[]")
    @Type(type = "list-array")
    @ArraySchema(arraySchema = @Schema(title = "Old Site codes"))
    private List<String> oldSiteCodes;

    @Basic
    @Column(name = "mpa")
    @Schema(title = "MPA")
    private String mpa;

    @Basic
    @Column(name = "protection_status")
    @Schema(title = "Protection status")
    private String protectionStatus;

    @Basic
    @Column(name = "relief")
    @Schema(title = "Relief")
    private Integer relief;

    @Basic
    @Column(name = "currents")
    @Schema(title = "Currents")
    private Integer currents;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> siteAttribute;

    @Basic
    @Column(name = "is_active")
    @Schema(title = "Active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @Schema(title = "Location", implementation = String.class, format = "uri")
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    /* Calculate geom from lat/lon when persisting to the db.  Ideally, we would just use geom in the db and */
    /* map to lat/long fields here */

    @PreUpdate
    @PrePersist
    public void calcGeom() {
        // Calculate geom field when persisting to the db
        if (longitude == null || latitude == null) {
            geom = null;
        } else {
            val factory = new GeometryFactory(new PrecisionModel(), 4326);
            geom = factory.createPoint(new Coordinate(longitude, latitude));
        }
    }
}
