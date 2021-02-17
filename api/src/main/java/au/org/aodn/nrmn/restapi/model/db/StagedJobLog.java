package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.StagedJobEventType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "staged_job_log")
public class StagedJobLog {
    @Id
    @SequenceGenerator(name = "staged_job_log_id_seq", sequenceName = "staged_job_log_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staged_job_log_id_seq")
    private Long id;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "staged_job_id", referencedColumnName = "id", nullable = false,
        foreignKey = @ForeignKey(name = "staged_job_log_staged_job_id_fkey"))
    @ToString.Exclude
    private StagedJob stagedJob;

    @Column(name = "event_time", columnDefinition = "timestamp with time zone", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp eventTime;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StagedJobEventType eventType;

    @Column(name = "details", columnDefinition = "text")
    private String details;

}
