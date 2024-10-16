package au.org.aodn.nrmn.restapi.data.model;

import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.org.aodn.nrmn.restapi.dto.correction.CorrectionDiffDto;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesSearchBodyDto;
import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Cache;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "staged_job_log")
@Cache(region = "entities", usage = CacheConcurrencyStrategy.READ_WRITE)
public class StagedJobLog {
    
    @Id
    @SequenceGenerator(name = "staged_job_log_id_seq", sequenceName = "staged_job_log_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staged_job_log_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "summary", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private CorrectionDiffDto summary;

    @Column(name = "filter_set", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private SpeciesSearchBodyDto filterSet;

}
