package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@Tag(name = "aphia refs")
public interface AphiaRefRepository extends JpaRepository<AphiaRef, Integer>, JpaSpecificationExecutor<AphiaRef>, EntityCriteria<AphiaRef> {

    @Override
    @Query("SELECT a from AphiaRef  a WHERE a.validName = :name")
    List<AphiaRef> findByCriteria(@Param("name") String name);

    @Override
    @RestResource
    Optional<AphiaRef> findById(Integer integer);

    @Override
    @RestResource
    Page<AphiaRef> findAll(Pageable pageable);
}
