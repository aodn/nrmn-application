package au.org.aodn.nrmn.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "aphia_rel_type_ref")
public class AphiaRelType {
    @Id
    @Column(name = "aphia_rel_type_id", unique = true, nullable = false)
    private Integer aphiaRelTypeId;

    @Basic
    @Column(name = "aphia_rel_type_name")
    private String aphiaRelTypeName;
}
