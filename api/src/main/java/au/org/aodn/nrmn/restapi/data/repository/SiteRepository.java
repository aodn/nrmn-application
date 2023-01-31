package au.org.aodn.nrmn.restapi.data.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.repository.model.EntityCriteria;

import javax.persistence.QueryHint;

import java.util.Collection;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@RepositoryRestResource
@Tag(name = "sites")
public interface SiteRepository
        extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site>, EntityCriteria<Site> {

    @Override
    @Query("SELECT s FROM Site s WHERE lower(s.siteCode) = lower(:code)")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    List<Site> findByCriteria(@Param("code") String siteCode);

    @Query(value = "SELECT LOWER(siteCode) FROM Site WHERE siteCode IN :siteCodes")
    List<String> getAllSiteCodesMatching(@Param("siteCodes") Collection<String> siteCodes);

    @Query("SELECT s FROM Site s")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    Collection<Site> getAll();

    @Query("SELECT s FROM Site s WHERE lower(s.siteCode) = lower(:code)")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    Site findBySiteCode(@Param("code") String siteCode);

    @Query(value = "SELECT DISTINCT state FROM Site WHERE state is not null ORDER BY state")
    List<String> findAllSiteStates();

    @Query(value = "SELECT DISTINCT country FROM Site WHERE country is not null ORDER BY country")
    List<String> findAllCountries();

    @Query(nativeQuery = true, value = "" +
            "SELECT sr.site_code || ' ' || '(' || sr.site_name || ' ' || ROUND(CAST(ST_Distance(CAST(st_makepoint(sr.longitude, sr.latitude) AS geography), CAST(st_makepoint(:longitude, :latitude) AS geography)) AS numeric), 2) || 'm)' " +
            "FROM {h-schema}site_ref sr " +
            "WHERE ST_DWithin(CAST(st_makepoint(sr.longitude, sr.latitude) AS geography), CAST(st_makepoint(:longitude, :latitude) AS geography), 200) " +
            "AND (sr.site_id <> :siteId)")
    List<String> sitesWithin200m(@Param("siteId") Integer siteId, @Param("longitude") double longitude, @Param("latitude") double latitude);

    @Query(value = "select ecoregion from {h-schema}meow_ecoregions, {h-schema}site_ref where ST_Contains(meow_ecoregions.geom, site_ref.geom) and site_ref.site_id = :site_id", nativeQuery = true)
    String getEcoregion(@Param("site_id") Integer siteId);
}
