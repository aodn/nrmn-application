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
@Table(name = "ui_species_attributes")
public class SpeciesWithAttributes {

    @Id
    @Column(name = "observable_item_id")
    private Integer id;

    private String letterCode;

    @Basic
    @Column(name = "species_name")
    private String speciesName;

    @Basic
    @Column(name = "common_name")
    private String commonName;

    @Basic
    @Column(name = "is_invert_sized")
    private Boolean isInvertSized;

    @Basic
    @Column(name = "L5")
    private Double l5;

    @Basic
    @Column(name = "L95")
    private Double l95;

    @Basic
    @Column(name = "lmax")
    private Integer lMax;
}
