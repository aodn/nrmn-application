package au.org.aodn.nrmn.restapi.data.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.restapi.data.model.MarineProtectedArea;

@RepositoryRestResource
@Tag(name = "marine protected areas")
public interface MarineProtectedAreaRepository extends JpaRepository<MarineProtectedArea, Integer>,
 JpaSpecificationExecutor<MarineProtectedArea> {

    @Override
    @RestResource
    Page<MarineProtectedArea> findAll(Pageable pageable);
    
}
