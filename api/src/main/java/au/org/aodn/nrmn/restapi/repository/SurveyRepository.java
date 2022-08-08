package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRowCacheable;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRowDivers;

import au.org.aodn.nrmn.restapi.repository.dynamicQuery.SurveyFilterCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer>, JpaSpecificationExecutor<Survey> {

//        @Query("SELECT new au.org.aodn.nrmn.restapi.repository.projections.SurveyRowCacheable(" +
//                "    sv.surveyId, " +
//                "    sv.surveyDate, " +
//                "    sv.surveyTime, " +
//                "    sv.depth, " +
//                "    sv.surveyNum, " +
//                "    sv.pqCatalogued, " +
//                "    sv.site.siteName, " +
//                "    sv.site.siteCode, " +
//                "    sv.site.mpa, " +
//                "    sv.site.country, " +
//                "    sv.program.programName, " +
//                "    sv.site.location.locationName " +
//                ") FROM Survey as sv " +
//                "WHERE (:surveyId is null or CAST(sv.surveyId as text) LIKE :surveyId) ORDER BY sv.surveyId DESC")
//        Page<SurveyRowCacheable> findAllProjectedBy(@Param("surveyId")  String surveyId, Pageable pageable);
        @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
        default Page<SurveyRowCacheable> findAllProjectedBy(List<Filter> filters, Pageable pageable) {

                return this.findAll(SurveyFilterCondition.createSpecification(filters), pageable).map(v ->
                        new SurveyRowCacheable(
                                v.getSurveyId(),
                                v.getSurveyDate(),
                                v.getSurveyTime(),
                                v.getDepth(),
                                v.getSurveyNum(),
                                v.getPqCatalogued(),
                                v.getSite().getSiteName(),
                                v.getSite().getSiteCode(),
                                v.getSite().getMpa(),
                                v.getSite().getCountry(),
                                v.getProgram().getProgramName(),
                                v.getSite().getLocation().getLocationName()));
        }


        @Query("SELECT t FROM #{#entityName} t WHERE t.id IN :ids")
        List<Survey> findByIdsIn(@Param("ids") List<Integer> ids);

        @Query("SELECT s FROM #{#entityName} s " + "WHERE s.site = :site " + "  AND s.depth = :depth "
                        + "  AND s.surveyNum = :surveyNum " + "  AND s.surveyDate = :date")
        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        List<Survey> findBySiteDepthSurveyNumDate(@Param("site") Site site, @Param("depth") Integer depth,
                        @Param("surveyNum") Integer surveyNum, @Param("date") Date date);

        @Query("SELECT s FROM Survey s WHERE s.surveyId IN :ids AND (s.pqCatalogued = FALSE OR s.pqCatalogued IS NULL)")
        List<Survey> findSurveysWithoutPQ(@Param("ids") List<Integer> ids);

        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        @Query("SELECT DISTINCT new au.org.aodn.nrmn.restapi.repository.projections.SurveyRowDivers(o.surveyMethod.survey.surveyId, o.diver.fullName) from Observation o where o.surveyMethod.survey.surveyId IN (:surveyIds)")
        List<SurveyRowDivers> getDiversForSurvey(@Param("surveyIds") List<Integer> surveyIds);

        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "false") })
        @Query("SELECT DISTINCT new au.org.aodn.nrmn.restapi.repository.projections.SurveyRowDivers(o.surveyMethod.survey.surveyId, o.diver.diverId) from Observation o where o.observationId IN (:observationIds)")
        List<SurveyRowDivers> getSurveyFromObservation(@Param("observationIds") List<Integer> surveyIds);

        @Modifying
        @Query("UPDATE Survey s SET s.updated = current_timestamp()")
        void updateSurveyModified();
}
