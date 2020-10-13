package au.org.aodn.nrmn.restapi.model.db;

import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "program_ref")
public class Program {
    @Id
    @SequenceGenerator(name = "program_ref_program_id", sequenceName = "program_ref_program_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "program_id", unique = true, updatable = false, nullable = false)
    private int programId;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "is_active")
    private Boolean isActive;
}
