package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ui_species_attributes")
public class UiSpeciesAttributes {

    @Id
    @Column(name = "observable_item_id")
    private Long id;

    private String speciesName;

    private String commonName;

    private Boolean isInvertSized;

    private Integer l5;

    private Integer l95;

    private Integer maxAbundance;

    private Integer lmax;

}
