package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    @Query(value = "select survey_date as surveyDate, survey_time as surveyTime, depth, survey_num as surveyNum, " +
                    "sv.survey_id as surveyId, st.site_name as siteName, pg.program_name as programName FROM {h-schema}survey " +
                    "sv LEFT JOIN {h-schema}program_ref pg ON pg.program_id = sv.program_id LEFT JOIN {h-schema}site_ref st ON st.site_id = sv.site_id "+
                    "ORDER BY surveyDate DESC",
        countQuery = "SELECT count(*) FROM {h-schema}survey",
        nativeQuery = true)
    List<SurveyRow> findAllProjectedBy();

    @Query("SELECT t FROM #{#entityName} t WHERE t.id IN :ids")
    List<Survey> findByIdsIn(@Param("ids") List<Integer> ids);

    @Query("SELECT s FROM #{#entityName} s " +
            "WHERE s.site = :site " +
            "  AND s.depth = :depth " +
            "  AND s.surveyNum = :surveyNum " +
            "  AND s.surveyDate = :date")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    List<Survey> findBySiteDepthSurveyNumDepth(
            @Param("site") Site site,
            @Param("depth") Integer depth,
            @Param("surveyNum") Integer surveyNum,
            @Param("date") Date date);
}
