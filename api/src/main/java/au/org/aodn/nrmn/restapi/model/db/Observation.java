package au.org.aodn.nrmn.restapi.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Map;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "observation")
@Audited(withModifiedFlag = true)
public class Observation {
    @Id
    @SequenceGenerator(name = "observation_observation_id", sequenceName = "observation_observation_id",
     allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="observation_observation_id")
    @Column(name = "observation_id", unique = true, updatable = false, nullable = false)
    private Integer observationId;

    @Basic
    @Column(name = "measure_value")
    private Integer measureValue;

    @Column(name = "observation_attribute", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private Map<String, String> observationAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_method_id", referencedColumnName = "survey_method_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private SurveyMethodEntity surveyMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diver_id", referencedColumnName = "diver_id")
    @JsonIgnore
    private Diver diver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observable_item_id", referencedColumnName = "observable_item_id", nullable = false)
    @JsonIgnore
    private ObservableItem observableItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measure_id", referencedColumnName = "measure_id", nullable = false)
    @Audited(targetAuditMode = NOT_AUDITED, withModifiedFlag = true)
    @JsonIgnore
    private Measure measure;
}
