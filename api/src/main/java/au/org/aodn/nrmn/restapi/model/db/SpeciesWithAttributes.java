package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;

@Entity
@Immutable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "species_attribute")
public class SpeciesWithAttributes {

    @Id
    @Column(name = "observable_item_id")
    private Long id;

    @Basic
    @Column(name = "observable_item_name")
    private String observableItemName;

    @Basic
    @Column(name = "common_name")
    private String commonName;

    @Basic
    @Column(name = "L5")
    private Double l5;

    @Basic
    @Column(name = "L95")
    private Double l95;

    @Basic
    @Column(name = "maxabundance")
    private Integer maxAbundance;

    @Basic
    @Column(name = "lmax")
    private Integer lMax;
}
