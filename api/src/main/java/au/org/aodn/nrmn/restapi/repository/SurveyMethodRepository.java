package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SurveyMethod;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyMethodRepository extends JpaRepository<SurveyMethod, Integer>,
 JpaSpecificationExecutor<SurveyMethod> {

     @Query(value = "SELECT method_id from {h-schema}survey_method WHERE survey_id = :surveyId", nativeQuery = true)
    List<String> findSurveyMethodsForSurveyId(@Param("surveyId") Integer surveyId);

    @Query(value = "SELECT '(' || method_id || ': ' || block_num || ')' from {h-schema}survey_method WHERE survey_id = :surveyId AND block_num is not null", nativeQuery = true)
    List<String> findBlocksForSurveyId(@Param("surveyId") Integer surveyId);

    @Query(value = "SELECT '(' || method_id || '-' || block_num || ': ' || survey_not_done || ')' from {h-schema}survey_method WHERE survey_id = :surveyId AND block_num is not null", nativeQuery = true)
    List<String> findSurveyNotDoneForSurveyId(@Param("surveyId") Integer surveyId);
}
