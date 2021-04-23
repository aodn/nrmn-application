package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "staged_job")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer"})
public class StagedJob implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staged_job_id_seq")
    @SequenceGenerator(name = "staged_job_id_seq", sequenceName = "staged_job_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "reference")
    private String reference;

    @Column(name = "is_extended_size")
    private Boolean isExtendedSize;


    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusJobType status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private SourceJobType source;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    private Program program;

    @Column(name = "created", columnDefinition = "timestamp with time zone")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp created;

    @Column(name = "last_updated", columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp lastUpdated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sec_user_id", referencedColumnName = "id", nullable = false)
    private SecUser creator;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stagedJob", cascade = CascadeType.ALL)
    private List<StagedJobLog> logs;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stagedJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StagedRow> rows;


    @Basic
    @Column(name = "survey_ids", columnDefinition = "integer[]")
    @Type(type = "list-array")
    private List<Integer> surveyIds;

}
