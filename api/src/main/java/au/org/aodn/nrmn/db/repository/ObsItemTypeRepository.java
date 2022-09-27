package au.org.aodn.nrmn.db.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.ObsItemType;
import io.swagger.v3.oas.annotations.tags.Tag;

@RepositoryRestResource
@Tag(name = "observable item types")
public interface ObsItemTypeRepository extends JpaRepository<ObsItemType, Integer>, JpaSpecificationExecutor<ObsItemType> {


    @Override
    @RestResource
    Optional<ObsItemType> findById(Integer integer);

    @Override
    @RestResource
    Page<ObsItemType> findAll(Pageable pageable);
}
