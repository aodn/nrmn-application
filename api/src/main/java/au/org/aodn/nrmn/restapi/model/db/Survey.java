package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.util.Map;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "survey")
@Audited(withModifiedFlag = true)
public class Survey {
    @Id
    @SequenceGenerator(name = "survey_survey_id", sequenceName = "survey_survey_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="survey_survey_id")
    @Column(name = "survey_id", unique = true, updatable = false, nullable = false)
    private Integer surveyId;

    @Basic
    @Column(name = "survey_date")
    private Date surveyDate;

    @Basic
    @Column(name = "survey_time")
    private Time surveyTime;

    @Basic
    @Column(name = "depth")
    private Integer depth;

    @Basic
    @Column(name = "survey_num")
    private Integer surveyNum;

    @Basic
    @Column(name = "visibility")
    private Integer visibility;

    @Basic
    @Column(name = "direction")
    private String direction;

    @Basic
    @Column(name = "survey_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> surveyAttribute;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", referencedColumnName = "program_id", nullable = false)
    @JsonIgnore
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    private Program program;
}
