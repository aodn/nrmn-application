package au.org.aodn.nrmn.restapi.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "obs_item_type_ref")
public class ObsItemType {
    @Id
    @SequenceGenerator(name = "obs_item_type_ref_obs_item_type_id", sequenceName = "obs_item_type_ref_obs_item_type_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="obs_item_type_ref_obs_item_type_id")
    @Column(name = "obs_item_type_id", unique = true, updatable = false, nullable = false)
    private Integer obsItemTypeId;

    @Basic
    @Column(name = "obs_item_type_name")
    private String obsItemTypeName;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive;
}
