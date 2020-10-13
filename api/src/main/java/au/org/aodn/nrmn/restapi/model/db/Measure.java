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
@Table(name = "measure_ref")
public class Measure {
    @Id
    @SequenceGenerator(name = "measure_ref_measure_id", sequenceName = "measure_ref_measure_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "measure_id", unique = true, updatable = false, nullable = false)
    private int measureId;

    @Basic
    @Column(name = "measure_name")
    private String measureName;

    @Basic
    @Column(name = "seq_no")
    private Integer seqNo;

    @Basic
    @Column(name = "is_active")
    private Boolean isActive;
}
