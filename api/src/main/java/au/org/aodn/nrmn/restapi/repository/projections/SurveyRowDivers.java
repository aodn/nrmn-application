package au.org.aodn.nrmn.restapi.repository.projections;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

import lombok.Value;

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Value
public class SurveyRowDivers {
    private Integer surveyId;
    private String diverName;

    public SurveyRowDivers(Integer surveyId, String diverName) {
        this.surveyId = surveyId;
        this.diverName = diverName;
    }
}
