package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.math.NumberUtils;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @NotNull
    @Column(name = "longitude")
    @Schema(title = "Longitude")
    private Double longitude;

    @Basic
    @NotNull
    @Column(name = "latitude")
    @Schema(title = "Latitude")
    private Double latitude;

    @Basic
    @Column(name = "geom")
    @Setter(AccessLevel.NONE)
    @Schema(hidden = true)
    @JsonIgnore
    private Point geom;

    @Column(name = "site_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    @Schema(title = "Attributes")
    private Map<String, Object> siteAttribute;

    @Basic
    @Column(name = "is_active")
    @Schema(title = "Active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @Schema(title = "Location")
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    /* We need a json schema for Map<String, String> for siteAttribute for the react json-schema form as it can't 
    /* handle Map<String, Object> */
    
    /* Requires a custom site_attribute mapping performed here as customising Hibernate JSON type mappings is */ 
    /* horrible.  Hopefully a short term workaround only as pulling editable attributes out into fields is preferable */
    /* and in the pipeline */
    
    /* Map OldSiteCodes to a comma separated String when getting siteAttribute */

    public Map<String, String> getSiteAttribute() {
        Function<Entry<String, Object>, String> asString = entry -> {
            if (entry.getKey().equals("OldSiteCodes")) {
                return String.join(",", (Collection)entry.getValue());
            } else {
                return entry.getValue() == null ? null : entry.getValue().toString();
            }
        };
        
        return siteAttribute == null ? null : siteAttribute.entrySet()
            .stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> asString.apply(e)
            ));
    }
    
    /* Map OldSiteCodes as a comma separated string to a List of strings and strings parsable as numbers to numbers  */
    /* when setting siteAttribute */
    
    public void setSiteAttribute(Map<String, String> value) {
        Function<Entry<String, String>, Object> asObject = entry -> {
            if (entry.getKey().equals("OldSiteCodes")) {
                return Arrays.asList((entry.getValue()).split(","));
            } else if (NumberUtils.isParsable(entry.getValue())) {
                return NumberUtils.createNumber(entry.getValue());
            } else {
                return entry.getValue();
            }
        };
        
        siteAttribute = value == null ? null : value.entrySet()
            .stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> asObject.apply(e)
            ));
    }
    
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
