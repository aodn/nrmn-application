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
@Table(name = "staged_file")
public class StagedJobEntity {

    @Id
    @Column(name = "file_id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusJobType status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private SourceJobType source;

    @Column(name="job_attributes")
    @Type(type = "jsonb")
    private Map<String, String> jobAttributes;
}
