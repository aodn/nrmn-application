package au.org.aodn.nrmn.restapi.repository.projections;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public interface SurveyRow {
    Integer getSurveyId();

    String getSiteName();

    String getProgramName();

    String getSurveyDate();

    String getSurveyTime();

    String getDepth();

    String getSurveyNum();

    String getSiteCode();

    String getHasPQs();

    String getMPA();

    String getCountry();

    String getDiverName();

    String getLocationName();
}
