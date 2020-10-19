package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
import java.util.Map;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "survey_method")
@Audited(withModifiedFlag = true)
public class SurveyMethod {
    @Id
    @SequenceGenerator(name = "survey_method_survey_method_id", sequenceName = "survey_method_survey_method_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "survey_method_id", unique = true, updatable = false, nullable = false)
    private int surveyMethodId;

    @Basic
    @Column(name = "block_num")
    private Integer blockNum;

    @Basic
    @Column(name = "survey_not_done")
    private Boolean surveyNotDone;

    @Basic
    @Column(name = "survey_method_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> surveyMethodAttribute;

    @OneToMany(mappedBy = "surveyMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Observation> observations;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", referencedColumnName = "survey_id", nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id", referencedColumnName = "method_id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @JsonIgnore
    private Method method;
}
