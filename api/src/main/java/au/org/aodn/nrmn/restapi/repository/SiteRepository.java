package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;



@RepositoryRestResource
@Tag(name = "sites")
public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site>, EntityCriteria<Site> {

    @Override
    @Query("SELECT s FROM Site s WHERE s.siteCode = :code")
    Optional<Site> findByCriteria(@Param("code")String siteCode);

    @Override
    @RestResource
    Page<Site> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends Site> S save(S s);

    @Override
    @RestResource
    Optional<Site> findById(Integer integer);
}
