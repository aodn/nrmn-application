package au.org.aodn.nrmn.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.NotAudited;
import org.locationtech.jts.geom.MultiPolygon;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "meow_ecoregions")
public class MeowEcoRegions {

    @Id
    @NotAudited
    private int id;

    @Column(name="ecoregion")
    @NotAudited
    private String ecoRegion;

    @Column(name="province")
    @NotAudited
    private String province;

    @Column(name="realm")
    @NotAudited
    private String realm;

    @Column(name="lat_zone")
    @NotAudited
    private String latZone;

    @Column(name="geom")
    @NotAudited
    private MultiPolygon polygon;
}
