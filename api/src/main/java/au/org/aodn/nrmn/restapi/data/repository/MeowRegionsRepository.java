package au.org.aodn.nrmn.restapi.data.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import au.org.aodn.nrmn.restapi.data.model.MeowEcoRegions;

public interface MeowRegionsRepository extends JpaRepository<MeowEcoRegions, Integer>, JpaSpecificationExecutor<MeowEcoRegions> {
    
    @Query(value = "WITH stp1 AS(" +
        "SELECT DISTINCT(ecoregion)AS region, species_name FROM {h-schema}ep_m2_cryptic_fish GROUP BY species_name,ecoregion) " +
        "SELECT DISTINCT species_name FROM stp1 WHERE species_name in (:species_names) AND region=:eco_region " +
        "", nativeQuery = true)
    Collection<String> getEcoregionContains(@Param("eco_region") String ecoRegion, @Param("species_names") Collection<String> speciesNames);
}
