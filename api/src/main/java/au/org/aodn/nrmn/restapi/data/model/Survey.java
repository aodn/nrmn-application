package au.org.aodn.nrmn.restapi.data.model;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@org.hibernate.annotations.Cache(region = "entities", usage = READ_WRITE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "survey")
@Audited(withModifiedFlag = true)
public class Survey {
    @Id
    @SequenceGenerator(name = "survey_survey_id", sequenceName = "survey_survey_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_survey_id")
    @Column(name = "survey_id", unique = true, updatable = false, nullable = false)
    @Schema(title = "Id")
    private Integer surveyId;

    @Basic
    @Column(name = "survey_date")
    @Schema(title = "Survey date")
    private Date surveyDate;

    @Column(name = "created", columnDefinition = "timestamp without time zone", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @Audited(targetAuditMode = NOT_AUDITED)
    private Timestamp created;

    @Column(name = "updated", columnDefinition = "timestamp without time zone", nullable = false)
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Timestamp updated;

    @Basic
    @Column(name = "survey_time")
    @Schema(title = "Survey time")
    private Time surveyTime;

    @Basic
    @Column(name = "depth")
    @Schema(title = "Depth")
    private Integer depth;

    @Basic
    @Column(name = "survey_num")
    @Schema(title = "Survey number")
    private Integer surveyNum;

    @Basic
    @Column(name = "visibility")
    @Schema(title = "Visibility")
    private Double visibility;

    @Basic
    @Column(name = "direction")
    @Schema(title = "Direction")
    private String direction;

    @Basic
    @Column(name = "longitude")
    @Schema(title = "Longitude")
    private Double longitude;

    @Basic
    @Column(name = "latitude")
    @Schema(title = "Latitude")
    private Double latitude;

    @Basic
    @Column(name = "protection_status")
    @Schema(title = "Protection status")
    private String protectionStatus;

    @Basic
    @Column(name = "inside_marine_park")
    @Schema(title = "Inside marine park")
    private String insideMarinePark;

    @Basic
    @Column(name = "notes")
    @Schema(title = "Notes")
    private String notes;

    @Basic
    @Column(name = "pq_catalogued")
    @Schema(title = "PQ catalogued")
    private Boolean pqCatalogued;

    @Basic
    @Column(name = "locked")
    @Schema(title = "Locked")
    private Boolean locked;

    @Basic
    @Column(name = "pq_zip_url")
    @Schema(title = "PQ zip url")
    private String pqZipUrl;

    @Basic
    @Column(name = "pq_diver_id")
    @Schema(title = "PQ Diver ID")
    private Integer pqDiverId;

    @Basic
    @Column(name = "block_abundance_simulated")
    @Schema(title = "Block abundance simulated")
    private Boolean blockAbundanceSimulated;

    @Basic
    @Column(name = "project_title")
    @Schema(title = "Project title")
    private String projectTitle;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
    @Schema(title = "Site")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    @JsonIgnore
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @Schema(title = "Program")
    private Program program;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    @Audited(targetAuditMode = NOT_AUDITED)
    private List<SurveyMethodEntity> surveyMethods;
}
