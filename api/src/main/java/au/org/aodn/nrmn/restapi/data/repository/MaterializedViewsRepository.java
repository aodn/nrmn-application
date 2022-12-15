package au.org.aodn.nrmn.restapi.data.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

@Transactional
@Tag(name = "materialized views")
public interface MaterializedViewsRepository extends JpaRepository<ObservableItem, Integer> {

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_m1'", nativeQuery = true)
    Boolean checkEpM1Running();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_m1;", nativeQuery = true)
    void refreshEpM1();

    @Query(value = "SELECT count(*) from nrmn.ep_m1;", nativeQuery = true)
    Long countEpM1();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m1 OFFSET :offset LIMIT :limit ;", nativeQuery = true)
    List<Tuple> getEpM1(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish'", nativeQuery = true)
    Boolean checkEpM2CrypticFishRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish;", nativeQuery = true)
    void refreshEpM2CrypticFish();

    // removed geom
    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m2_cryptic_fish;", nativeQuery = true)
    List<Tuple> getEpM2CrypticFish();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts'", nativeQuery = true)
    Boolean checkEpM2InvertsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts;", nativeQuery = true)
    void refreshEpM2Inverts();

    @Query(value = "SELECT  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m2_inverts;", nativeQuery = true)
    List<Tuple> getEpM2Inverts();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_observable_items'", nativeQuery = true)
    Boolean checkEpObservableItemsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_observable_items;", nativeQuery = true)
    void refreshEpObservableItems();

    @Query(value = "SELECT * from nrmn.ep_observable_items;", nativeQuery = true)
    List<Tuple> getEpObservableItems();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance'", nativeQuery = true)
    Boolean checkEpRarityAbundanceRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance;", nativeQuery = true)
    void refreshEpRarityAbundance();

    @Query(value = "SELECT * from nrmn.ep_rarity_abundance;", nativeQuery = true)
    List<Tuple> getEpRarityAbundance();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents'", nativeQuery = true)
    Boolean checkEpRarityExtentsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents;", nativeQuery = true)
    void refreshEpRarityExtents();

    @Query(value = "SELECT taxon, CAST(geom AS varchar), CAST(points AS varchar), mean_latitude, mean_longitude, CAST(mean_point AS varchar), km_degr_vertical, km_degr_horizontal from nrmn.ep_rarity_extents;", nativeQuery = true)
    List<Tuple> getEpRarityExtents();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency'", nativeQuery = true)
    Boolean checkEpRarityFrequencyRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency;", nativeQuery = true)
    void refreshEpRarityFrequency();

    @Query(value = "SELECT * from nrmn.ep_rarity_frequency;", nativeQuery = true)
    List<Tuple> getEpRarityFrequency();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range'", nativeQuery = true)
    Boolean checkEpRarityRangeRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range;", nativeQuery = true)
    void refreshEpRarityRange();

    @Query(value = "SELECT * from nrmn.ep_rarity_range;", nativeQuery = true)
    List<Tuple> getEpRarityRange();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_site_list'", nativeQuery = true)
    Boolean checkEpSiteListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_site_list;", nativeQuery = true)
    void refreshEpSiteList();

    @Query(value = "SELECT country, area, location, mpa, site_code, site_name, old_site_codes, latitude, longitude, wave_exposure, relief, slope, currents, realm, province, ecoregion, lat_zone, CAST(geom AS varchar), programs, protection_status from nrmn.ep_site_list;", nativeQuery = true)
    List<Tuple> getEpSiteList();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_survey_list'", nativeQuery = true)
    Boolean checkEpSurveyListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_survey_list;", nativeQuery = true)
    void refreshEpSurveyList();

    @Query(value = "SELECT survey_id, country, area, location, mpa, site_code, site_name, latitude, longitude, depth, survey_date, latest_surveydate_for_site, has_pq_scores_in_db, has_rugosity_scores_in_db, has_pqs_catalogued_in_db, divers, visibility, hour, direction, survey_latitude, survey_longitude, avg_rugosity, max_rugosity, surface, CAST(geom AS varchar), program, pq_zip_url, protection_status, old_site_codes, methods, survey_notes from nrmn.ep_survey_list;", nativeQuery = true)
    List<Tuple> getEpSurveyList();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query LIKE 'REFRESH MATERIALIZED VIEW %'", nativeQuery = true)
    Boolean checkAnyRunning();
}
