package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.SurveyMethodEntity;

@Repository
public interface SurveyMethodRepository extends JpaRepository<SurveyMethodEntity, Integer>,
 JpaSpecificationExecutor<SurveyMethodEntity> {

     @Query(value = "SELECT DISTINCT method_id from {h-schema}survey_method " +
             "WHERE survey_id = :surveyId AND survey_not_done = FALSE " +
             "ORDER BY method_id", nativeQuery = true)
    List<String> findSurveyMethodsForSurveyId(@Param("surveyId") Integer surveyId);

    @Query(value = "SELECT method_id || ': ' || block_num from {h-schema}survey_method " +
            "WHERE survey_id = :surveyId AND block_num is not null AND survey_not_done = FALSE " +
            "ORDER BY method_id, block_num", nativeQuery = true)
    List<String> findBlocksForSurveyId(@Param("surveyId") Integer surveyId);

    @Query(value = "SELECT method_id || ': ' || block_num from {h-schema}survey_method " +
            "WHERE survey_id = :surveyId AND block_num is not null AND survey_not_done = TRUE " +
            "ORDER BY method_id, block_num", nativeQuery = true)
    List<String> findSurveyNotDoneForSurveyId(@Param("surveyId") Integer surveyId);

    @Query(value = "select sm from SurveyMethodEntity sm where sm.survey.surveyId = :surveyId and sm.method.methodId = " +
     ":methodId and sm.blockNum = :blockNum")
    Optional<SurveyMethodEntity> findBySurveyIdMethodIdBlockNum(@Param("surveyId") Integer surveyId,
     @Param("methodId") Integer methodId, @Param("blockNum") Integer blockNum);

    @Query("SELECT DISTINCT concat(d.fullName, ' (', d.initials, ')') FROM Observation o JOIN o.diver d WHERE o.surveyMethod.survey.surveyId = :surveyId")
    List<String> findDiversForSurvey(@Param("surveyId") Integer surveyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SurveyMethodEntity WHERE survey_id = :surveyId")
    void deleteForSurveyId(@Param("surveyId") Integer surveyId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SurveyMethodEntity WHERE survey_id IN :surveyId AND method_id NOT IN (6, 13)")
    void deleteForSurveyIds(@Param("surveyId") List<Integer> surveyIds);
}
