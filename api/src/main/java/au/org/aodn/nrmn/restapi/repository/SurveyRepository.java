package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyFilterDto;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRow;
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

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

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
        default Page<SurveyRowCacheable> findAllProjectedBy(Filter[] filters, Pageable pageable) {

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

        @Query(value = 
                "SELECT DISTINCT survey_date as surveyDate, survey_time as surveyTime, depth, survey_num as surveyNum, " +
                "s.survey_id as surveyId, t.site_name as siteName, p.program_name as programName " +
                "FROM {h-schema}survey s " +
                "LEFT JOIN {h-schema}site_ref t ON s.site_id = t.site_id " +
                "LEFT JOIN {h-schema}ep_site_list l ON t.site_code = l.site_code " +
                "LEFT JOIN {h-schema}survey_method m ON s.survey_id = m.survey_id " +
                "LEFT JOIN {h-schema}program_ref p ON p.program_id = s.program_id " +
                "LEFT JOIN {h-schema}ep_species_survey v ON s.survey_id = v.survey_id WHERE " +
                "s.survey_date >= CAST(?#{#f.startDateTimestamp} as date) AND " +
                "s.survey_date <= CAST(?#{#f.endDateTimestamp}   as date) AND " +
                "(?#{#f.siteId}     IS NULL OR s.site_id     = (CAST (CAST(?#{#f.siteId} AS character varying) AS integer)))             AND " +
                "(?#{#f.surveyId}   IS NULL OR s.survey_id   = (CAST (CAST(?#{#f.surveyId} AS character varying) AS integer)))           AND " +
                "(?#{#f.depth}      IS NULL OR s.depth       = (CAST (CAST(?#{#f.depth} AS character varying) AS integer)))              AND " +
                "(?#{#f.diverId}    IS NULL OR s.pq_diver_id = (CAST (CAST(?#{#f.diverId} AS character varying) AS integer)))            AND " +
                "(?#{#f.programId}  IS NULL OR s.program_id  = (CAST (CAST(?#{#f.programId} AS character varying) AS integer)))          AND " +
                "(?#{#f.latitude}   IS NULL OR s.latitude    = (CAST (CAST(?#{#f.latitude} AS character varying) AS double precision)))  AND " +
                "(?#{#f.longitude}  IS NULL OR s.longitude   = (CAST (CAST(?#{#f.longitude} AS character varying) AS double precision))) AND " +
                "(?#{#f.locationId} IS NULL OR t.location_id = (CAST (CAST(?#{#f.locationId} AS character varying) AS integer)))         AND " +
                "(?#{#f.country}    IS NULL OR t.country     = (CAST(?#{#f.country} AS character varying)))                              AND " +
                "(?#{#f.state}      IS NULL OR t.state       = (CAST(?#{#f.state} AS character varying)))                                AND " +
                "(?#{#f.siteCode}   IS NULL OR t.site_code   = (CAST(?#{#f.siteCode} AS character varying)))                             AND " +
                "(?#{#f.ecoRegion}  IS NULL OR l.ecoregion   = (CAST(?#{#f.ecoRegion} AS character varying)))                            AND " +
                "(?#{#f.methodId}   IS NULL OR m.method_id   = (CAST (CAST(?#{#f.methodId} AS character varying) AS integer)))           AND " +
                "(?#{#f.speciesId}  IS NULL OR v.species_id  = (CAST (CAST(?#{#f.speciesId} AS character varying) AS integer))) "              + 
                "ORDER BY surveyId DESC", nativeQuery = true)
        List<SurveyRow> findByCriteria(@Param("f") SurveyFilterDto surveyFilter);

        @Query("SELECT s FROM Survey s WHERE s.surveyId IN :ids AND (s.pqCatalogued = FALSE OR s.pqCatalogued IS NULL)")
        List<Survey> findSurveysWithoutPQ(@Param("ids") List<Integer> ids);

        @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
        @Query("SELECT DISTINCT new au.org.aodn.nrmn.restapi.repository.projections.SurveyRowDivers(o.surveyMethod.survey.surveyId, o.diver.fullName) from Observation o where o.surveyMethod.survey.surveyId IN (:surveyIds)")
        List<SurveyRowDivers> getDiversForSurvey(@Param("surveyIds") List<Integer> surveyIds);

        @Modifying
        @Query("UPDATE Survey s SET s.updated = current_timestamp()")
        void updateSurveyModified();
}
