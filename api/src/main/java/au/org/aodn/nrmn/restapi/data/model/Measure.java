package au.org.aodn.nrmn.restapi.data.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cache(region = "entities", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "measure_ref")
public class Measure {
    @Id
    @SequenceGenerator(name = "measure_ref_measure_id", sequenceName = "measure_ref_measure_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="measure_ref_measure_id")
    @Column(name = "measure_id", unique = true, updatable = false, nullable = false)
    private Integer measureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_type_id", referencedColumnName = "measure_type_id")
    private MeasureTypeEntity measureType;
    
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
