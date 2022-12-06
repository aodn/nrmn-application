package au.org.aodn.nrmn.restapi.data.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * This is used to create an aggregate string for diver name and survey method. Right now it is only use by
 * SurveyListView.
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        "SELECT sm.survey_id, " +
        "     string_agg(DISTINCT sm.method_id::text,', ' ORDER BY sm.method_id::text ASC) as method, " +
        "     string_agg(DISTINCT dr.full_name, ', ' ORDER BY dr.full_name ASC) as diver " +
        "FROM nrmn.observation o " +
        "     INNER JOIN nrmn.survey_method sm on o.survey_method_id = sm.survey_method_id " +
        "     INNER JOIN nrmn.diver_ref dr on dr.diver_id = o.diver_id " +
        "GROUP BY sm.survey_id"
)
public class DiverMethodAggregateView {
    @Id
    @Column(name = "survey_id")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Integer surveyId;

    @Column(name = "method")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String method;

    @Column(name = "diver")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String fullName;

    @JsonGetter
    public String getDiverName() {
        return fullName;
    }

    @JsonGetter
    public String getMethod() {
        return method;
    }
}
