package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Location;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "locations")
public interface LocationRepository extends JpaRepository<Location, Integer>, JpaSpecificationExecutor<Location> {

    @Override
    @RestResource
    Page<Location> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends Location> S save(S s);

    @Override
    @RestResource
    Optional<Location> findById(Integer integer);
}
