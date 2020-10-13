package au.org.aodn.nrmn.restapi.model.db;

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
@Table(name = "measure_type_ref")
public class MeasureType {
    @Id
    @SequenceGenerator(name = "measure_type_ref_measure_id", sequenceName = "measure_type_ref_measure_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "measure_type_id", unique = true, updatable = false, nullable = false)
    private int measureTypeId;

    @Column(name = "measure_type_name")
    private String measureTypeName;

    @Column(name = "is_active")
    private Boolean isActive;
}
