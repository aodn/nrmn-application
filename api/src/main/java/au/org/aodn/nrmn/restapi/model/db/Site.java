package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Cache;
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

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;

@Entity
@Cache(region = "entities", usage = READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "site_ref")
@Audited(withModifiedFlag = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Site {
    @Id
    @SequenceGenerator(name = "site_ref_site_id", sequenceName = "site_ref_site_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "site_ref_site_id")
    @Schema(hidden = true)
    @Column(name = "site_id", unique = true, updatable = false, nullable = false)
    private Integer siteId;

    @Basic
    @NotNull
    @Column(name = "site_code")
    @Schema(title = "Site Code", required = true)
    private String siteCode;

    @Basic
    @NotNull
    @Column(name = "site_name")
    @Schema(title = "Site Name", required = true)
    private String siteName;

    @Basic
    @Column(name = "old_site_code", columnDefinition = "varchar(50)[]")
    @Type(type = "list-array")
    @ArraySchema(arraySchema = @Schema(title = "Old Site Codes"))
    private List<String> oldSiteCodes;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @Schema(title = "Location", implementation = String.class, format = "uri")
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    @Basic
    @Column(name = "state")
    @Schema(title = "State", required = true)
    private String state;

    @Basic
    @Column(name = "country")
    @Schema(title = "Country", required = true)
    private String country;

    @Basic
    @Column(name = "latitude")
    @Schema(title = "Latitude", required = true, minimum="-90", maximum="90")
    private Double latitude;

    @Basic
    @Column(name = "longitude")
    @Schema(title = "Longitude", required = true, minimum="-180", maximum="180")
    private Double longitude;

    @Basic
    @Column(name = "mpa")
    @Schema(title = "Marine Protected Area")
    private String mpa;

    @Basic
    @Column(name = "protection_status")
    @Schema(title = "Protection Status")
    private String protectionStatus;

    @Basic
    @Column(name = "relief")
    @Schema(title = "Relief")
    private Integer relief;

    @Basic
    @Column(name = "slope")
    @Schema(title = "Slope")
    private Integer slope;

    @Basic
    @Column(name = "wave_exposure")
    @Schema(title = "Wave exposure")
    private Integer waveExposure;

    @Basic
    @Column(name = "currents")
    @Schema(title = "Currents")
    private Integer currents;

    @Basic
    @Column(name = "is_active", columnDefinition = "boolean default false")
    @Schema(title = "Is Active")
    private Boolean isActive = false;

    @Basic
    @Column(name = "geom")
    @Setter(AccessLevel.NONE)
    @Schema(hidden = true)
    @JsonIgnore
    private Point geom;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    @Schema(title = "Other attributes", accessMode = Schema.AccessMode.READ_ONLY)
    private Map<String, String> siteAttribute;

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
