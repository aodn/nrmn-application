package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table("staged_file")
public class StagingJob {

    @Id
    @Column("file_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private StatusJobType status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SourceJobType source;

    @Column(name="job_attributes")
    @Type(type = "jsonb")
    private Map<String, String> jobAttributes;
}
