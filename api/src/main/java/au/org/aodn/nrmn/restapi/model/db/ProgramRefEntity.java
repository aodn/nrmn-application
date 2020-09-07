package au.org.aodn.nrmn.restapi.model.db;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "program_ref"  )
public class ProgramRefEntity {
    @Id
    @Column(name = "program_id")
    private int programId;

    @Column(name = "program_name")
    private String programName;
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "program", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PublicDataExclusionEntity> publicDataExclusions;
}
