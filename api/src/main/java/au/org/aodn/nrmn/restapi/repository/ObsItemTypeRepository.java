package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
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
@Tag(name = "observable item types")
public interface ObsItemTypeRepository extends JpaRepository<ObsItemType, Integer>,
    JpaSpecificationExecutor<ObsItemType> {

    @Override
    @RestResource
    Optional<ObsItemType> findById(Integer integer);

    @Override
    @RestResource
    Page<ObsItemType> findAll(Pageable pageable);
}
