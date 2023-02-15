package au.org.aodn.nrmn.restapi.data.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.org.aodn.nrmn.restapi.enums.StagedJobEventType;

import org.hibernate.annotations.CreationTimestamp;

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

    @Column(name = "survey_id")
    private Integer surveyId;

    @Column(name = "row_summary")
    private String rowSummary;
}
