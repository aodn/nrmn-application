package au.org.aodn.nrmn.restapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "program_ref")
public class Program  implements Serializable {
    @Id
    @SequenceGenerator(name = "program_ref_program_id", sequenceName = "program_ref_program_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="program_ref_program_id")
    @Column(name = "program_id", unique = true, updatable = false, nullable = false)
    private Integer programId;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "is_active")
    private Boolean isActive;
}
