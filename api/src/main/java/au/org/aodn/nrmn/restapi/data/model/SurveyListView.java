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
 * This view is use by the GUI site screen, we do not use the survey db entity directly because
 * we want to allow flexible table join here if needed in future
 */
@Entity
@Immutable
@Audited(withModifiedFlag = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Subselect(
        "SELECT s.survey_id, sr.site_code, to_char(s.survey_date, 'yyyy-MM-dd') as survey_date, " +
                "       (s.depth || '.' || s.survey_num) as depth, sr.site_name, pr.program_name, " +
                "       lr.location_name, CAST((s.pq_catalogued IS NOT NULL) as varchar) as pq_catalogued, COALESCE(sr.mpa, '') as mpa, sr.country, d.diver as full_name " +
                "FROM nrmn.survey s " +
                "INNER JOIN nrmn.site_ref sr ON s.site_id = sr.site_id " +
                "INNER JOIN nrmn.program_ref pr ON s.program_id = pr.program_id " +
                "INNER JOIN nrmn.location_ref lr ON sr.location_id = lr.location_id " +
                "LEFT OUTER JOIN ( " +  // Allow missing diver with outer join
                "    SELECT sm.survey_id, string_agg(DISTINCT dr.full_name, ', ' ORDER BY dr.full_name ASC) as diver " +
                "    FROM nrmn.observation o " +
                "        INNER JOIN nrmn.survey_method sm on o.survey_method_id = sm.survey_method_id " +
                "        INNER JOIN nrmn.diver_ref dr on dr.diver_id = o.diver_id " +
                "    GROUP BY sm.survey_id) d on d.survey_id = s.survey_id"
)
public class SurveyListView {
    @Id
    @Column(name = "survey_id")
    @Audited(targetAuditMode = NOT_AUDITED)
    private Integer surveyId;

    @Column(name = "site_code")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String siteCode;

    @Column(name = "survey_date")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String surveyDate;

    @Column(name = "depth")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String depth;

    @Column(name = "site_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String siteName;

    @Column(name = "program_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String programName;

    @Column(name = "location_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String locationName;

    @Column(name = "pq_catalogued")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String pqCatalogued;

    @Column(name = "country")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String country;

    @Column(name = "mpa")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String mpa;

    @Column(name = "full_name")
    @Audited(targetAuditMode = NOT_AUDITED)
    private String fullName;

    @JsonGetter
    public Integer getSurveyId() {
        return surveyId;
    }

    @JsonGetter
    public String getSiteCode() {
        return siteCode;
    }

    @JsonGetter
    public String getSurveyDate() {
        return surveyDate;
    }

    @JsonGetter
    public String getDepth() {
        return depth;
    }

    @JsonGetter
    public String getSiteName() {
        return siteName;
    }

    @JsonGetter
    public String getProgramName() {
        return programName;
    }

    @JsonGetter
    public String getLocationName() {
        return locationName;
    }

    @JsonGetter
    public String getHasPQs() {
        return pqCatalogued;
    }

    @JsonGetter
    public String getCountry() {
        return country;
    }

    @JsonGetter
    public String getDiverName() {
        return fullName;
    }

    @JsonGetter
    public String getMpa() {
        return mpa;
    }
}
