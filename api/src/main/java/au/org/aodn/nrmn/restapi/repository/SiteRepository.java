package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.projections.SiteList;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(excerptProjection = SiteList.class)
@Tag(name = "sites")
public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site>, EntityCriteria<Site> {

    @Override
    @Query("SELECT s FROM Site s WHERE s.siteCode = :code")
    Optional<Site> findByCriteria(@Param("code") String siteCode);

    @Override
    @RestResource
    Page<Site> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends Site> S save(S s);

    @Override
    @RestResource
    Optional<Site> findById(Integer integer);

    @Override
    @RestResource
    void delete(Site site);

    @Query(nativeQuery = true, value = "SELECT site_code FROM {h-schema}ep_site_list WHERE province = ?1")
    Collection<String> findSiteCodesByProvince(String province);

}
