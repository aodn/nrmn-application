package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowDto;

@Repository
public interface CorrectionRowRepository
        extends JpaRepository<Observation, Long>, JpaSpecificationExecutor<Observation> {

    @Query("SELECT DISTINCT s.program FROM Survey s where s.id IN :surveyIds")
    List<Program> findProgramsBySurveyIds(@Param("surveyIds") List<Integer> surveyIds);

    @Query(value = "" +
    "SELECT c.survey_id AS surveyId, c.survey_num AS surveyNum, c.diver_id AS diverId, c.initials AS diver, c.pq_initials AS pqDiver, " +
    "c.site_code AS siteCode, c.site_name AS siteName, CAST(c.depth AS text) || '.' || CAST(c.survey_num AS text) AS depth, TO_CHAR(c.survey_date, 'dd/MM/yyyy') AS DATE, " +
    "TO_CHAR(c.survey_time, 'HH24:MI') AS time, c.visibility AS vis, c.direction, c.latitudeA as latitude, c.longitudeA as longitude, " +
    "c.observable_item_name AS species, c.common_name as commonName, LOWER(c.letter_code) AS code, " +
    "c.method_id AS method, c.block_num as block, SUM(c.measure_value) AS total, " +
    "(CASE WHEN measure_type_id = 4 THEN 'Yes' ELSE 'No' END) AS isInvertSizing, " +
    "CAST(jsonb_object_agg(c.seq_no, c.measure_value) AS TEXT) AS measureJson, " +
    "(CASE WHEN c.letter_code = 'SND' THEN '' ELSE CAST(jsonb_agg(c.observation_id) AS TEXT) END) AS observationIds " +
	"FROM " +
	"(SELECT " +
    "COALESCE(i.observable_item_name, 'Survey Not Done') AS observable_item_name, " +
    "COALESCE(m.block_num, 0) as block_num, " +
	"COALESCE(r.seq_no, 0) AS seq_no, " +
	"COALESCE(o.measure_value, 0) as measure_value, " +
    "ROUND(CAST(COALESCE(s.latitude, t.latitude) as numeric), 5) as latitudeA, " +
    "ROUND(CAST(COALESCE(s.longitude, t.longitude) as numeric),5) as longitudeA, " +
	"CASE WHEN o.observable_item_id IS NULL THEN 'SND' ELSE i.letter_code END AS letter_code, " +
	"m.method_id, o.observation_id, " +
	"s.survey_id, o.diver_id, d.initials, s.survey_num, e.initials as pq_initials, t.site_code, t.site_name, s.depth, s.survey_date, s.survey_time, " +
    "s.visibility, s.direction, i.common_name, " +
	"mt.measure_type_id FROM nrmn.survey_method m " +
    "LEFT JOIN nrmn.observation o ON o.survey_method_id = m.survey_method_id " +
    "LEFT JOIN nrmn.observable_item_ref i ON o.observable_item_id = i.observable_item_id " +
    "LEFT JOIN nrmn.diver_ref d ON o.diver_id    = d.diver_id " +
    "LEFT JOIN nrmn.survey s ON s.survey_id = m.survey_id JOIN nrmn.site_ref t ON s.site_id = t.site_id " +
    "LEFT JOIN nrmn.diver_ref e ON s.pq_diver_id = e.diver_id " +
    "LEFT JOIN nrmn.measure_ref r ON r.measure_id = o.measure_id " +
    "LEFT JOIN nrmn.measure_type_ref mt ON r.measure_type_id = mt.measure_type_id " +
    "WHERE m.survey_id IN :surveyIds AND m.method_id NOT IN (6, 13) " +
	"GROUP BY m.method_id, m.block_num, o.measure_id, seq_no, mt.measure_type_id,o.observation_id, " +
	"s.survey_id, o.diver_id, d.initials, pq_initials, s.survey_num, e.initials, t.site_code, t.site_name, s.depth, s.survey_date, s.survey_time, " +
	"s.visibility, s.direction, s.longitude, t.longitude, s.latitude, t.latitude, o.observable_item_id, observable_item_name, i.common_name, letter_code) c " +
    "GROUP BY c.survey_id, c.survey_num, c.diver_id, c.initials, c.pq_initials, c.site_code, c.site_name, c.depth, c.survey_date, c.survey_time, c.visibility, " +
    "c.direction, c.latitudeA, c.longitudeA, c.observable_item_name, c.common_name, c.letter_code, c.method_id, c.block_num, c.measure_type_id " +
    "", nativeQuery = true)
    
    List<CorrectionRowDto> findRowsBySurveyIds(@Param("surveyIds") List<Integer> surveyIds);
}
