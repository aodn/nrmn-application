package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

@Transactional
@Tag(name = "materialized views")
public interface MaterializedViewsRepository extends JpaRepository<AphiaRef, Integer>, JpaSpecificationExecutor<AphiaRef> {

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_m1'", nativeQuery = true)
    Boolean checkEpM1Running();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_m1;", nativeQuery = true)
    void refreshEpM1();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish'", nativeQuery = true)
    Boolean checkEpM2CrypticFishRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish;", nativeQuery = true)
    void refreshEpM2CrypticFish();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts'", nativeQuery = true)
    Boolean checkEpM2InvertsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts;", nativeQuery = true)
    void refreshEpM2Inverts();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_observable_items'", nativeQuery = true)
    Boolean checkEpObservableItemsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_observable_items;", nativeQuery = true)
    void refreshEpObservableItems();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance'", nativeQuery = true)
    Boolean checkEpRarityAbundanceRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance;", nativeQuery = true)
    void refreshEpRarityAbundance();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents'", nativeQuery = true)
    Boolean checkEpRarityExtentsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents;", nativeQuery = true)
    void refreshEpRarityExtents();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency'", nativeQuery = true)
    Boolean checkEpRarityFrequencyRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency;", nativeQuery = true)
    void refreshEpRarityFrequency();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range'", nativeQuery = true)
    Boolean checkEpRarityRangeRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range;", nativeQuery = true)
    void refreshEpRarityRange();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_site_list'", nativeQuery = true)
    Boolean checkEpSiteListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_site_list;", nativeQuery = true)
    void refreshEpSiteList();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_survey_list'", nativeQuery = true)
    Boolean checkEpSurveyListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_survey_list;", nativeQuery = true)
    void refreshEpSurveyList();


    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes'", nativeQuery = true)
    Boolean checkUiSpeciesAttributesRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes;", nativeQuery = true)
    void refreshUiSpeciesAttributes();


}
