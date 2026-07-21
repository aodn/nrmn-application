package au.org.aodn.nrmn.restapi.data.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;

import java.util.List;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

/**
 * Queries for the nrmn.ep_*_public views defined in db/endpoints/CreatePublicEndpoints.sql.
 * PublicViewService publishes these to the IMOS data landing bucket.
 *
 * Columns are listed out rather than using SELECT * because these views expose a PostGIS geometry
 * column, which Hibernate cannot map into a Tuple ("No Dialect mapping for JDBC type: 1111"), so
 * geom has to be cast. Keep the lists in step with CreatePublicEndpoints.sql.
 */
@Transactional
@Tag(name = "public views")
public interface PublicViewsRepository extends JpaRepository<ObservableItem, Integer> {

    // The m0, m1 and m2 public views all expose the same columns, so they share one list.
    String OBSERVATION_COLUMNS = "survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, method, block, phylum, class, \"order\", family, species_name, reporting_name, size_class, total, biomass";

    String M3_ISQ_COLUMNS = "survey_id, country, area, ecoregion, realm, location, site_code, site_name, latitude, longitude, survey_date, depth, CAST(geom AS varchar), program, visibility, hour, survey_latitude, survey_longitude, phylum, class, \"order\", family, species_name, reporting_name, report_group, habitat_groups, quadrat, total";

    String SITE_LIST_COLUMNS = "country, area, location, site_code, site_name, old_site_codes, latitude, longitude, realm, province, ecoregion, lat_zone, CAST(geom AS varchar), programs";

    String SURVEY_LIST_COLUMNS = "survey_id, country, area, location, site_code, site_name, latitude, longitude, depth, survey_date, latest_surveydate_for_site, has_pq_scores_in_db, has_rugosity_scores_in_db, has_pqs_catalogued_in_db, visibility, hour, direction, survey_latitude, survey_longitude, avg_rugosity, max_rugosity, surface, CAST(geom AS varchar), program, pq_zip_url, old_site_codes, methods";

    @Query(value = "SELECT count(*) from nrmn.ep_m0_off_transect_sighting_public;", nativeQuery = true)
    Long countEpM0OffTransectSightingPublic();

    @Query(value = "SELECT " + OBSERVATION_COLUMNS + " from nrmn.ep_m0_off_transect_sighting_public "
            +
            "ORDER BY " + OBSERVATION_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM0OffTransectSightingPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m1_public;", nativeQuery = true)
    Long countEpM1Public();

    @Query(value = "SELECT " + OBSERVATION_COLUMNS + " from nrmn.ep_m1_public "
            +
            "ORDER BY " + OBSERVATION_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM1Public(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m2_cryptic_fish_public;", nativeQuery = true)
    Long countEpM2CrypticFishPublic();

    @Query(value = "SELECT " + OBSERVATION_COLUMNS + " from nrmn.ep_m2_cryptic_fish_public "
            +
            "ORDER BY " + OBSERVATION_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM2CrypticFishPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m2_inverts_public;", nativeQuery = true)
    Long countEpM2InvertsPublic();

    @Query(value = "SELECT " + OBSERVATION_COLUMNS + " from nrmn.ep_m2_inverts_public "
            +
            "ORDER BY " + OBSERVATION_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM2InvertsPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_m3_isq_public;", nativeQuery = true)
    Long countEpM3IsqPublic();

    @Query(value = "SELECT " + M3_ISQ_COLUMNS + " from nrmn.ep_m3_isq_public "
            +
            "ORDER BY " + M3_ISQ_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpM3IsqPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_site_list_public;", nativeQuery = true)
    Long countEpSiteListPublic();

    @Query(value = "SELECT " + SITE_LIST_COLUMNS + " from nrmn.ep_site_list_public "
            +
            "ORDER BY " + SITE_LIST_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpSiteListPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);

    // ------------------

    @Query(value = "SELECT count(*) from nrmn.ep_survey_list_public;", nativeQuery = true)
    Long countEpSurveyListPublic();

    @Query(value = "SELECT " + SURVEY_LIST_COLUMNS + " from nrmn.ep_survey_list_public "
            +
            "ORDER BY " + SURVEY_LIST_COLUMNS + " OFFSET :offset LIMIT :limit", nativeQuery = true)
    List<Tuple> getEpSurveyListPublic(@Param("offset") Integer offset, @Param("limit") Integer limit);
}
