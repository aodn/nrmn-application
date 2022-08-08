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
    private Integer diverId;
    private String diverName;

    public SurveyRowDivers(Integer surveyId, String diverName) {
        this.surveyId = surveyId;
        this.diverId = null;
        this.diverName = diverName;
    }

    public SurveyRowDivers(Integer surveyId, Integer diverId) {
        this.surveyId = surveyId;
        this.diverId = diverId;
        this.diverName = null;
    }
}
