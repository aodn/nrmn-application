package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.geo.Polygon;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Entity
@Cache(region = "entities", usage = READ_ONLY)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "meow_ecoregions")
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeowEcoregions {

    @Id
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Integer meowId;

    @Column(name = "ecoregion")
    @NotNull
    private String ecoRegion;

    @Column(name = "province")
    @NotNull
    private String province;

    @Column(name = "realm")
    @NotNull
    private String realm;

    @Column(name = "lat_zone")
    @NotNull
    private String latitudeZone;

    @Column(columnDefinition = "Geometry")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Polygon polygon;
}
