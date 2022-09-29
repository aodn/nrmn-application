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
 * This view is use by the GUI diver screen, we do not use the Diver db entity directly because
 * we want to allow flexible table join here if needed in future
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        "SELECT d.diver_id, d.initials, d.full_name FROM nrmn.diver_ref d ORDER BY (CASE WHEN initials SIMILAR TO '%[a-zA-Z]' THEN 0 ELSE 1 END), LOWER(d.initials)"
)
public class DiverListView {
    @Id
    @Column(name="diver_id")
    private int diverId;

    @Column(name="initials")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String initials;

    @Column(name="full_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String fullName;

    @JsonGetter
    public int getDiverId() {
        return diverId;
    }

    @JsonGetter
    public String getInitials() {
        return initials;
    }

    @JsonGetter
    public String getFullName() {
        return fullName;
    }
}
