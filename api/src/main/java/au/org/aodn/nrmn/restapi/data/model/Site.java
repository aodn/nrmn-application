package au.org.aodn.nrmn.restapi.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import au.org.aodn.nrmn.restapi.enums.Iirc;

@Entity
@Cache(region = "entities", usage = CacheConcurrencyStrategy.READ_WRITE)
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
    @Column(name = "site_id", unique = true, updatable = false, nullable = false)
    private Integer siteId;

    @Basic
    @NotNull
    @Column(name = "site_code")
    private String siteCode;

    @Basic
    @NotNull
    @Column(name = "site_name")
    private String siteName;

    @Basic
    @Column(name = "old_site_code", columnDefinition = "varchar(50)[]")
    @Type(type = "list-array")
    private List<String> oldSiteCodes;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    @Basic
    @Column(name = "state")
    private String state;

    @Basic
    @Column(name = "country")
    private String country;

    @Basic
    @Column(name = "latitude")
    private Double latitude;

    @Basic
    @Column(name = "longitude")
    private Double longitude;

    @Basic
    @Column(name = "mpa")
    private String mpa;

    @Basic
    @Column(name = "protection_status")
    private String protectionStatus;

    @Basic
    @Column(name = "relief")
    private Integer relief;

    @Basic
    @Column(name = "slope")
    private Integer slope;

    @Basic
    @Column(name = "wave_exposure")
    private Integer waveExposure;

    @Basic
    @Column(name = "currents")
    private Integer currents;

    @Basic
    @Column(name = "is_active", columnDefinition = "boolean default false")
    private Boolean isActive = false;

    @Basic
    @Column(name = "geom")
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private Point geom;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> siteAttribute;

    /*
     * Calculate geom from lat/lon when persisting to the db. Ideally, we would just
     * use geom in the db and
     */
    /* map to lat/long fields here */
    @PreUpdate
    @PrePersist
    public void calcGeom() {
        // Calculate geom field when persisting to the db
        if (longitude == null || latitude == null) {
            geom = null;
        } else {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            geom = factory.createPoint(new Coordinate(longitude, latitude));
        }

        if(longitude != null) {
            longitude = new BigDecimal(longitude).setScale(Iirc.ROUNDING_DIGIT, RoundingMode.HALF_UP).doubleValue();
        }

        if(latitude != null) {
            latitude = new BigDecimal(latitude).setScale(Iirc.ROUNDING_DIGIT, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
