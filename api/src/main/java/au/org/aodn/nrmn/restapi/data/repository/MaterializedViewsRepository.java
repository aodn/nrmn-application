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

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m1 "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM1(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish'", nativeQuery = true)
    Boolean checkEpM2CrypticFishRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish;", nativeQuery = true)
    void refreshEpM2CrypticFish();

    @Query(value = "SELECT count(*) from nrmn.ep_m2_cryptic_fish;", nativeQuery = true)
    Long countEpM2CrypticFish();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m2_cryptic_fish "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM2CrypticFish(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts'", nativeQuery = true)
    Boolean checkEpM2InvertsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts;", nativeQuery = true)
    void refreshEpM2Inverts();

    @Query(value = "SELECT count(*) from nrmn.ep_m2_inverts;", nativeQuery = true)
    Long countEpM2Inverts();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m2_inverts "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM2Inverts(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_observable_items'", nativeQuery = true)
    Boolean checkEpObservableItemsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_observable_items;", nativeQuery = true)
    void refreshEpObservableItems();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance'", nativeQuery = true)
    Boolean checkEpRarityAbundanceRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance;", nativeQuery = true)
    void refreshEpRarityAbundance();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents'", nativeQuery = true)
    Boolean checkEpRarityExtentsRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents;", nativeQuery = true)
    void refreshEpRarityExtents();

    @Query(value = "SELECT count(*) from nrmn.ep_rarity_extents;", nativeQuery = true)
    Long countEpRarityExtents();

    @Query(value = "SELECT taxon, CAST(geom AS varchar), CAST(points AS varchar), mean_latitude, mean_longitude, CAST(mean_point AS varchar), km_degr_vertical, km_degr_horizontal from nrmn.ep_rarity_extents "
            +
            "ORDER BY taxon, CAST(geom AS varchar), CAST(points AS varchar), mean_latitude, mean_longitude, CAST(mean_point AS varchar), km_degr_vertical, km_degr_horizontal  OFFSET :offset LIMIT :limit ", nativeQuery = true)
    List<Tuple> getEpRarityExtents(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency'", nativeQuery = true)
    Boolean checkEpRarityFrequencyRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW CONCURRENTLY nrmn.ep_rarity_frequency;", nativeQuery = true)
    void refreshEpRarityFrequency();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range'", nativeQuery = true)
    Boolean checkEpRarityRangeRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range;", nativeQuery = true)
    void refreshEpRarityRange();

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_site_list'", nativeQuery = true)
    Boolean checkEpSiteListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_site_list;", nativeQuery = true)
    void refreshEpSiteList();

    @Query(value = "SELECT count(*) from nrmn.ep_site_list;", nativeQuery = true)
    Long countEpSiteList();

    @Query(value = "SELECT country, area, location, mpa, site_code, site_name, old_site_codes, latitude, longitude, wave_exposure, relief, slope, currents, realm, province, ecoregion, lat_zone, CAST(geom AS varchar), programs, protection_status from nrmn.ep_site_list "
            +
            "ORDER BY country, area, location, mpa, site_code, site_name, old_site_codes, latitude, longitude, wave_exposure, relief, slope, currents, realm, province, ecoregion, lat_zone, CAST(geom AS varchar), programs, protection_status OFFSET :offset LIMIT :limit ;", nativeQuery = true)
    List<Tuple> getEpSiteList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query = 'REFRESH MATERIALIZED VIEW nrmn.ep_survey_list'", nativeQuery = true)
    Boolean checkEpSurveyListRunning();

    @Modifying
    @Query(value = "REFRESH MATERIALIZED VIEW nrmn.ep_survey_list;", nativeQuery = true)
    void refreshEpSurveyList();

    @Query(value = "SELECT COUNT(*) FROM nrmn.ep_survey_list;", nativeQuery = true)
    Long countEpSurveyList();

    @Query(value = "SELECT survey_id, country, area, location, mpa, site_code, site_name, latitude, longitude, depth, survey_date, latest_surveydate_for_site, has_pq_scores_in_db, has_rugosity_scores_in_db, has_pqs_catalogued_in_db, divers, visibility, hour, direction, survey_latitude, survey_longitude, avg_rugosity, max_rugosity, surface, CAST(geom AS varchar), program, pq_zip_url, protection_status, old_site_codes, methods, survey_notes from nrmn.ep_survey_list "
            +
            "ORDER BY survey_id, country, area, location, mpa, site_code, site_name, latitude, longitude, depth, survey_date, latest_surveydate_for_site, has_pq_scores_in_db, has_rugosity_scores_in_db, has_pqs_catalogued_in_db, divers, visibility, hour, direction, survey_latitude, survey_longitude, avg_rugosity, max_rugosity, surface, CAST(geom AS varchar), program, pq_zip_url, protection_status, old_site_codes, methods, survey_notes  OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpSurveyList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m0_off_transect_sighting;", nativeQuery = true)
    Long countEpM0OffTransectSighting();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass from nrmn.ep_m0_off_transect_sighting "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, method, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total, biomass OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM0OffTransectSighting(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_observable_items;", nativeQuery = true)
    Long countEpObservableItems();

    @Query(value = "SELECT observable_item_id, observable_item_name, obs_item_type_name, phylum, class, \"order\", family, genus, common_name, range, frequency, abundance, max_length, common_family_name, common_class_name, common_phylum_name, superseded_by, superseded_ids, superseded_names, a, b, cf, aphia_relation, aphia_id, scientificname, status, unacceptreason, taxon, reporting_name, report_group, habitat_groups, other_groups, mapped_id from nrmn.ep_observable_items "
            +
            "ORDER BY observable_item_id, observable_item_name, obs_item_type_name, phylum, class, \"order\", family, genus, common_name, range, frequency, abundance, max_length, common_family_name, common_class_name, common_phylum_name, superseded_by, superseded_ids, superseded_names, a, b, cf, aphia_relation, aphia_id, scientificname, status, unacceptreason, taxon, reporting_name, report_group, habitat_groups, other_groups, mapped_id OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpObservableItems(@Param("offset") Integer offset, @Param("limit") Integer limit);
    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_rarity_abundance;", nativeQuery = true)
    Long countEpRarityAbundance();

    @Query(value = "SELECT  taxon, n_sites, n_surveys, n_blocks, abundance from nrmn.ep_rarity_abundance " +
            "ORDER BY  taxon, n_sites, n_surveys, n_blocks, abundance OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpRarityAbundance(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_rarity_range;", nativeQuery = true)
    Long countEpRarityRange();

    @Query(value = "SELECT  taxon, num_sites, range from nrmn.ep_rarity_range " +
            "ORDER BY  taxon, num_sites, range OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpRarityRange(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m3_isq;", nativeQuery = true)
    Long countEpM3Isq();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, report_group, habitat_groups, quadrat, total from nrmn.ep_m3_isq "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, report_group, habitat_groups, quadrat, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM3Isq(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m4_macrocystis_count;", nativeQuery = true)
    Long countEpM4Macrocystis();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, block, total from nrmn.ep_m4_macrocystis_count "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, block, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM4Macrocystis(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m5_limpet_quadrats;", nativeQuery = true)
    Long countEpM5LimpetQuadrats();

    @Query(value = "SELECT  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, quadrat, total from nrmn.ep_m5_limpet_quadrats "
            +
            "ORDER BY  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, quadrat, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM5LimpetQuadrats(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m7_lobster_count;", nativeQuery = true)
    Long countEpM7LobsterCount();

    @Query(value = "SELECT  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total from nrmn.ep_m7_lobster_count "
            +
            "ORDER BY  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM7LobsterCount(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m11_off_transect_measurement;", nativeQuery = true)
    Long countEpM11OffTransectMeasurement();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total from nrmn.ep_m11_off_transect_measurement "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, phylum, class, \"order\", family, recorded_species_name, species_name, taxon, reporting_name, size_class, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM11OffTransectMeasurement(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m12_debris;", nativeQuery = true)
    Long countEpM12Debris();

    @Query(value = "SELECT  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, debris, total from nrmn.ep_m12_debris "
            +
            "ORDER BY  survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, diver, block, debris, total OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM12Debris(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m13_pq_scores;", nativeQuery = true)
    Long countEpM13PqScores();

    @Query(value = "SELECT survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, resolution, category, major_category, num_points, total_points, percent_cover from nrmn.ep_m13_pq_scores "
            +
            "ORDER BY survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, resolution, category, major_category, num_points, total_points, percent_cover OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM13PqScores(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_species_survey_observation;", nativeQuery = true)
    Long countEpSpeciesSurveyObservation();

    @Query(value = "SELECT species_id, survey_id, site_code, latitude, longitude, site_name, ecoregion, province, realm, country, area, location, survey_date, depth, CAST(geom AS varchar), program, diver, total, block, method_id, size_class, biomass, mapped_id from nrmn.ep_species_survey_observation "
            +
            "ORDER BY species_id, survey_id, site_code, latitude, longitude, site_name, ecoregion, province, realm, country, area, location, survey_date, depth, CAST(geom AS varchar), program, diver, total, block, method_id, size_class, biomass, mapped_id OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpSpeciesSurveyObservation(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT COUNT(*) > 0 FROM pg_stat_activity WHERE query LIKE 'REFRESH MATERIALIZED VIEW %'", nativeQuery = true)
    Boolean checkAnyRunning();
}
