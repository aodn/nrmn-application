package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.Observation;
import au.org.aodn.nrmn.restapi.data.model.UiSpeciesAttributes;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ObservationRepository
        extends JpaRepository<Observation, Integer>, JpaSpecificationExecutor<Observation> {

    @Query(nativeQuery = true, value = "SELECT DISTINCT ON (species_name) observable_item_id as id, species_name as speciesName, common_name " +
            "as commonName, is_invert_sized as isInvertSized, l5, l95, maxabundance as maxAbundance, lmax " +
            "FROM  nrmn.ui_species_attributes   where observable_item_id in :id")
    List<UiSpeciesAttributes> getSpeciesAttributesByIds(@Param("id") int[] id);

    @Query("SELECT observationId FROM Observation where surveyMethod.survey.surveyId = :surveyId AND surveyMethod.method.methodId NOT IN (6, 13) ORDER BY observationId")
    List<Integer> findObservationIdsForSurvey(@Param("surveyId") Integer surveyId);

	@Modifying
    @Query(nativeQuery = true, value = "" +
    "update nrmn.observation " +
    "set observable_item_id = :newId " +
    "from nrmn.survey_method " +
    "where observation.survey_method_id = survey_method.survey_method_id " +
    "and observable_item_id = :oldId and survey_id = :surveyId")
	void updateObservableItemsForSurveys(@Param("surveyId") Integer surveyId, @Param("oldId") Integer oldId, @Param("newId") Integer newId);
}
