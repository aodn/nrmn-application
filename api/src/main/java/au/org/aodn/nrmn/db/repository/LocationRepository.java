package au.org.aodn.nrmn.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.Location;
import io.swagger.v3.oas.annotations.tags.Tag;

@RepositoryRestResource
@Tag(name = "locations")
public interface LocationRepository extends JpaRepository<Location, Integer>, JpaSpecificationExecutor<Location> {

    @Override
    @RestResource
    <S extends Location> S save(S s);

    @Override
    @RestResource
    Optional<Location> findById(Integer integer);
}
