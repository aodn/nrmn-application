package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.HabitatGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
@Tag(name = "habitat groups")
public interface HabitatGroupRepository extends JpaRepository<HabitatGroup, Integer>,
 JpaSpecificationExecutor<HabitatGroup> {

    @Override
    @RestResource
    Page<HabitatGroup> findAll(Pageable pageable);

}
