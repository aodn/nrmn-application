package au.org.aodn.nrmn.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRowDto;
import au.org.aodn.nrmn.restapi.model.db.Observation;

@Repository
public interface CorrectionRowRepository
        extends JpaRepository<Observation, Long>, JpaSpecificationExecutor<Observation> {

    @Query(value = "select cast(jsonb_agg(c.observation_id) as text) as observationIds, c.survey_id as surveyId, c.survey_num as surveyNum, " +
            "c.diver_id as diverId, c.initials as diver, " +
            "c.site_code as siteCode, c.depth, TO_CHAR(c.survey_date, 'dd/MM/yyyy') as date, TO_CHAR(c.survey_time, 'HH24:MI') as time, c.visibility as vis, "
            +
            "c.direction, " +
            "c.latitude, c.longitude, " +
            "c.observable_item_id as observableItemId, c.observable_item_name as species, c.letter_code as letterCode, " +
            "c.method_id as method, c.block_num as block, c.survey_not_done as surveyNotDone, " +
            "(CASE WHEN measure_type_id = 4 THEN true ELSE false END) as isInvertSizing, " +
            "cast(jsonb_object_agg(c.seq_no, c.measure_sum) as text) as measureJson " +
            "from ( " +
            "select " +
            "o.observation_id, s.survey_id, s.survey_num, o.diver_id, d.initials, " +
            "t.site_code, s.depth, s.survey_date, s.survey_time, s.visibility, " +
            "s.direction, " +
            "s.latitude, s.longitude, " +
            "o.observable_item_id, i.observable_item_name, i.letter_code, " +
            "m.method_id, m.block_num, m.survey_not_done, " +
            "o.measure_id, r.seq_no, sum(o.measure_value) as measure_sum, mt.measure_type_id " +
            "from nrmn.observation o " +
            "join nrmn.survey_method m on o.survey_method_id = m.survey_method_id " +
            "join nrmn.observable_item_ref i on o.observable_item_id = i.observable_item_id " +
            "join nrmn.diver_ref d on o.diver_id = d.diver_id " +
            "join nrmn.survey s on s.survey_id = m.survey_id " +
            "join nrmn.site_ref t on s.site_id = t.site_id " +
            "join nrmn.measure_ref r on r.measure_id = o.measure_id " +
            "join nrmn.measure_type_ref mt on r.measure_type_id = mt.measure_type_id " +
            "where m.survey_id = :surveyId " +
            "group by " +
            "o.observation_id, s.survey_id, s.survey_num, o.diver_id, d.initials, " +
            "t.site_code, s.depth, s.survey_date, s.survey_time, s.visibility, " +
            "s.direction, " +
            "s.latitude, s.longitude, r.seq_no, " +
            "o.observable_item_id, i.observable_item_name, i.letter_code, " +
            "m.method_id, m.block_num, m.survey_not_done, o.measure_id, mt.measure_type_id" +
            ") c " +
            "group by c.survey_id, c.survey_num, c.diver_id, c.initials, " +
            "c.site_code, c.depth, c.survey_date, c.survey_time, c.visibility, " +
            "c.direction, " +
            "c.latitude, c.longitude, " +
            "c.observable_item_id, c.observable_item_name, c.letter_code, " +
            "c.method_id, c.block_num, c.survey_not_done, c.measure_type_id", nativeQuery = true)
    List<CorrectionRowDto> findRowsBySurveyId(@Param("surveyId") Integer surveyId);
}
