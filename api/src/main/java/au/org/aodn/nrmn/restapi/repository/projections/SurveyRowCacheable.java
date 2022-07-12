package au.org.aodn.nrmn.restapi.repository.projections;

import java.util.Date;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

import lombok.Value;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Value
public class SurveyRowCacheable {
    private Integer surveyId;
    private String siteName;
    private Date surveyDate;
    private Date surveyTime;
    private String depth;
    private String siteCode;
    private String hasPQs;
    private String mpa;
    private String country;
    private String programName;
    private String locationName;

    public SurveyRowCacheable(Integer surveyId, Date surveyDate, Date surveyTime, Integer depth, Integer surveyNum, Boolean hasPQs, String siteName, String siteCode, String mpa, String country, String programName, String locationName) {
        this.surveyId = surveyId;
        this.siteName = siteName;
        this.surveyDate = surveyDate;
        this.surveyTime = surveyTime;
        this.depth = depth.toString() + "." + surveyNum.toString();
        this.siteCode = siteCode;
        this.hasPQs = hasPQs != null ? "true" : "false";
        this.mpa = mpa;
        this.country = country;
        this.programName = programName;
        this.locationName = locationName;
    }
}
