package au.org.aodn.nrmn.restapi.repository;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;

@Repository
public interface ObservationRepository
        extends JpaRepository<Observation, Integer>, JpaSpecificationExecutor<Observation> {

    @Query(nativeQuery = true, value ="select a.observable_item_id as id, a.species_name as speciesName, a.common_name as commonName, " + 
    "a.is_invert_sized as isInvertSized, a.l5 as l5, a.l95 as l95, a.maxabundance as maxAbundance, a.lmax as lmax " + 
    "from nrmn.ui_species_attributes a join nrmn.observable_item_ref o on a.observable_item_id = o.observable_item_id where o.observable_item_name = :name")
    Optional<UiSpeciesAttributes> getSpeciesAttributesBySpeciesName(@Param("name") String name);

    @Query(nativeQuery = true, value ="select a.observable_item_id as id, a.species_name as speciesName, a.common_name as commonName, " + 
    "a.is_invert_sized as isInvertSized, a.l5 as l5, a.l95 as l95, a.maxabundance as maxAbundance, a.lmax as lmax " + 
    "from nrmn.ui_species_attributes a join nrmn.observable_item_ref o on a.observable_item_id = o.observable_item_id where o.observable_item_name in :names")
    List<UiSpeciesAttributes> getSpeciesAttributesBySpeciesNames(@Param("names") Collection<String> names);

    @Query(nativeQuery = true, value = "SELECT  observable_item_id as id, species_name as speciesName, common_name " +
            "as commonName, is_invert_sized as isInvertSized, l5, l95, maxabundance as maxAbundance, lmax " +
            "FROM  nrmn.ui_species_attributes   where observable_item_id = :id")
    Optional<UiSpeciesAttributes> getSpeciesAttributesById(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "SELECT DISTINCT ON (species_name) observable_item_id as id, species_name as speciesName, common_name " +
            "as commonName, is_invert_sized as isInvertSized, l5, l95, maxabundance as maxAbundance, lmax " +
            "FROM  nrmn.ui_species_attributes   where observable_item_id in :id")
    List<UiSpeciesAttributes> getSpeciesAttributesByIds(@Param("id") int[] id);

    @Query("SELECT observationId FROM Observation where surveyMethod.survey.surveyId = :surveyId")
    List<Integer> findObservationIdsForSurvey(@Param("surveyId") Integer surveyId);
}
