package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRelType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "aphia relationship types")
public interface AphiaRelTypeRepository extends JpaRepository<AphiaRelType, Integer>,
    JpaSpecificationExecutor<AphiaRelType> {

    @Override
    @RestResource
    Page<AphiaRelType> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends AphiaRelType> S save(S s);

    @Override
    @RestResource
    Optional<AphiaRelType> findById(Integer integer);
}
