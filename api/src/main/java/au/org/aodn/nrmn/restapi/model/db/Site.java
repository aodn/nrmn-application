package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="site_ref_site_id")
    @Column(name = "site_id", unique = true, updatable = false, nullable = false)
    private Integer siteId;

    @Basic
    @Column(name = "site_code")
    private String siteCode;

    @Basic
    @Column(name = "site_name")
    private String siteName;

    @Basic
    @Column(name = "longitude")
    private Double longitude;

    @Basic
    @Column(name = "latitude")
    private Double latitude;

    @Basic
    @Column(name = "geom")
    @Setter(AccessLevel.NONE)
    @Schema(hidden = true)
    @JsonIgnore
    private Point geom;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, Object> siteAttribute;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    @PreUpdate
    @PrePersist
    public void calcGeom() {
        // Calculate geom field when persisting to the db
        val factory = new GeometryFactory(new PrecisionModel(), 4326);
        geom = factory.createPoint(new Coordinate(longitude, latitude));
    }
}
