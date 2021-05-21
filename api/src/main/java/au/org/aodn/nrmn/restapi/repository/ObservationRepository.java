package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository
        extends JpaRepository<Observation, Integer>, JpaSpecificationExecutor<Observation> {

    @Query(nativeQuery = true, value = "SELECT  observable_item_id as id,species_name, common_name, is_invert_sized, l5, l95, maxabundance as max_abundance, lmax FROM  nrmn.ui_species_attributes   where observable_item_id = :id")
    List<UiSpeciesAttributes> getSpeciesAttributesById(@Param("id") Long id);

    @Query(nativeQuery = true, value = "SELECT  observable_item_id as id,species_name as speciesName, common_name as commonName, is_invert_sized as isInvertSized, l5, l95, maxabundance as maxAbundance, lmax FROM  nrmn.ui_species_attributes   where observable_item_id in :id")
    List<UiSpeciesAttributes> getSpeciesAttributesByIds(@Param("id") List<Integer> id);

}
