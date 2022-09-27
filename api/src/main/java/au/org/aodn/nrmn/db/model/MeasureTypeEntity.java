package au.org.aodn.nrmn.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "measure_type_ref")
public class MeasureTypeEntity {
    @Id
    @SequenceGenerator(name = "measure_type_ref_measure_id", sequenceName = "measure_type_ref_measure_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="measure_type_ref_measure_id")
    @Column(name = "measure_type_id", unique = true, updatable = false, nullable = false)
    private Integer measureTypeId;

    @Column(name = "measure_type_name")
    private String measureTypeName;

    @Column(name = "is_active")
    private Boolean isActive;
}
