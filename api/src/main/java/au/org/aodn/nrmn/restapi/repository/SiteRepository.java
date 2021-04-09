package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@RepositoryRestResource
@Tag(name = "sites")
public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site>, EntityCriteria<Site> {

    @Override
    @Query("SELECT s FROM Site s WHERE lower(s.siteCode) = lower(:code)")
    @QueryHints({@QueryHint(name = HINT_CACHEABLE, value = "true")})
    List<Site> findByCriteria(@Param("code") String siteCode);

    @Query(nativeQuery = true, value = "SELECT site_code FROM {h-schema}ep_site_list WHERE province = ?1")
    Collection<String> findSiteCodesByProvince(String province);

    @Query(value = "SELECT DISTINCT siteCode FROM Site where siteCode is not null")
    Collection<String> findAllSiteCodes();

    @Query(value = "SELECT DISTINCT state FROM Site WHERE state is not null")
    Collection<String> findAllSiteStates();

    @Query(nativeQuery = true, value = "SELECT DISTINCT province FROM {h-schema}ep_site_list where province is not null")
    Collection<String> findAllSiteProvinces();

    <T> Optional<T> findBySiteId(Integer id, Class<T> type);
}
