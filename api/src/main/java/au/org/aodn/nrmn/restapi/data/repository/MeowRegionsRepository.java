package au.org.aodn.nrmn.restapi.data.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import au.org.aodn.nrmn.restapi.data.model.MeowEcoRegions;

public interface MeowRegionsRepository
        extends JpaRepository<MeowEcoRegions, Integer>, JpaSpecificationExecutor<MeowEcoRegions> {

    @Query(value = "select ecoregion" +
    "from {h-schema}observable_item_ref oi " +
    "inner join {h-schema}observation o ON o.observable_item_id = oi.observable_item_id " +
    "inner join {h-schema}survey_method sm ON o.survey_method_id = sm.survey_method_id " +
    "inner join {h-schema}survey s ON sm.survey_id = s.survey_id " +
    "inner join {h-schema}site_ref si ON s.site_id = si.site_id " +
    "inner join {h-schema}meow_ecoregions m ON st_contains(m.geom, si.geom) " +
    "where observable_item_name in (:species_names) AND ecoregion = :eco_region", nativeQuery = true)
    Collection<String> getEcoregionContains(@Param("eco_region") String ecoRegion, @Param("species_names") Collection<String> speciesNames);
}
