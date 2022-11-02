package au.org.aodn.nrmn.restapi.data.repository;

import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.model.SurveyListView;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.SurveyFilterCondition;
import au.org.aodn.nrmn.restapi.data.repository.projections.SurveyRowCacheable;
import au.org.aodn.nrmn.restapi.data.repository.projections.SurveyRowDivers;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer>, JpaSpecificationExecutor<Survey> {

        @Query("SELECT s.surveyId FROM #{#entityName} s " + "WHERE s.site = :site " + "  AND s.depth = :depth "
                        + "  AND s.surveyNum = :surveyNum " + "  AND s.surveyDate = :date")
        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        List<Long> findBySiteDepthSurveyNumDate(@Param("site") Site site, @Param("depth") Integer depth,
                        @Param("surveyNum") Integer surveyNum, @Param("date") Date date);

        @Query("SELECT s FROM Survey s WHERE s.surveyId IN :ids AND (s.pqCatalogued = FALSE OR s.pqCatalogued IS NULL)")
        List<Survey> findSurveysWithoutPQ(@Param("ids") List<Integer> ids);

        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        @Query("SELECT DISTINCT new au.org.aodn.nrmn.restapi.data.repository.projections.SurveyRowDivers(o.surveyMethod.survey.surveyId, o.diver.fullName) from Observation o where o.surveyMethod.survey.surveyId IN (:surveyIds)")
        List<SurveyRowDivers> getDiversForSurvey(@Param("surveyIds") List<Integer> surveyIds);

        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        @Query("SELECT DISTINCT o.surveyMethod.survey.surveyId from Observation o where o.observationId IN (:observationIds)")
        List<Integer> getSurveyIdForObservation(@Param("observationIds") List<Integer> observationIds);

        @Modifying
        @Query("UPDATE Survey s SET s.updated = current_timestamp() WHERE s.surveyId IN :ids")
        void updateSurveyModified(@Param("ids") List<Integer> ids);
}
