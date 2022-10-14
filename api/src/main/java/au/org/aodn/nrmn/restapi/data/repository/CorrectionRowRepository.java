package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowDto;

@Repository
public interface CorrectionRowRepository
        extends JpaRepository<Observation, Long>, JpaSpecificationExecutor<Observation> {

    @Query(value = "SELECT" +
    " c.survey_id AS surveyId, c.survey_num AS surveyNum, c.diver_id AS diverId," +
    " c.initials AS diver, c.pq_initials AS pqDiver, c.site_code AS siteCode, CAST(c.depth AS text) || '.' || CAST(c.survey_num AS text) AS depth, TO_CHAR(c.survey_date, 'dd/MM/yyyy') AS DATE," +
    " TO_CHAR(c.survey_time, 'HH24:MI') AS time, c.visibility AS vis, c.direction, c.latitude, c.longitude," +
    " c.observable_item_id AS observableItemId, c.observable_item_name AS species, c.letter_code AS code," +
    " c.method_id AS method, c.block_num as block," +
    " (CASE WHEN measure_type_id = 4 THEN 'Yes' ELSE 'No' END) AS isInvertSizing," +
    " (CASE WHEN c.letter_code = 'SND' THEN '' ELSE CAST(jsonb_agg(c.observation_id) AS text) END) AS observationIds," +
    " CAST(jsonb_object_agg(c.seq_no, c.measure_sum) AS TEXT) AS measureJson" +
    " FROM" +
    " (SELECT o.observation_id, s.survey_id, s.survey_num, o.diver_id, d.initials, e.initials as pq_initials, t.site_code, s.depth, s.survey_date, s.survey_time," +
    "   s.visibility, s.direction, s.latitude, s.longitude, o.observable_item_id," +
    "   COALESCE(i.observable_item_name, 'Survey Not Done') AS observable_item_name," +
    "   CASE WHEN o.observation_id IS NULL THEN 'SND' ELSE i.letter_code END," +
    "   m.method_id, COALESCE(m.block_num, 0) as block_num, o.measure_id, COALESCE(r.seq_no, 0) AS seq_no, COALESCE(SUM(o.measure_value), 0)" +
    "   AS measure_sum, mt.measure_type_id FROM nrmn.survey_method m" +
    "   LEFT JOIN nrmn.observation o ON o.survey_method_id = m.survey_method_id" +
    "   LEFT JOIN nrmn.observable_item_ref i ON o.observable_item_id = i.observable_item_id" +
    "   LEFT JOIN nrmn.diver_ref d ON o.diver_id    = d.diver_id" +
    "   LEFT JOIN nrmn.survey s ON s.survey_id = m.survey_id JOIN nrmn.site_ref t ON s.site_id = t.site_id" +
    "   LEFT JOIN nrmn.diver_ref e ON s.pq_diver_id = e.diver_id" +
    "   LEFT JOIN nrmn.measure_ref r ON r.measure_id = o.measure_id" +
    "   LEFT JOIN nrmn.measure_type_ref mt ON r.measure_type_id = mt.measure_type_id" +
    "   WHERE m.survey_id IN :surveyIds" +
    "   GROUP BY o.observation_id, s.survey_id, s.survey_num, o.diver_id, d.initials, e.initials, t.site_code, s.depth, s.survey_date, s.survey_time," +
    "   s.visibility, s.direction, s.latitude, s.longitude, r.seq_no, o.observable_item_id, i.observable_item_name, i.letter_code," +
    "   m.method_id, m.block_num, o.measure_id, mt.measure_type_id) c" +
    " GROUP BY c.survey_id, c.survey_num, c.diver_id, c.initials, c.pq_initials, c.site_code, c.depth, c.survey_date, c.survey_time, c.visibility," +
    " c.direction, c.latitude, c.longitude, c.observable_item_id, c.observable_item_name, c.letter_code, c.method_id, c.block_num, c.measure_type_id" +
    " ORDER BY c.method_id, c.block_num;", nativeQuery = true)
    
    List<CorrectionRowDto> findRowsBySurveyIds(@Param("surveyIds") List<Integer> surveyIds);
}
