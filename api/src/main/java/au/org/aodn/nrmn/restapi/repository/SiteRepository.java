package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Site;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@Tag(name = "sites")
public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site> {
    List<Site> findBySiteCode(String siteCode);

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
