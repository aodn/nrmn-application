package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import au.org.aodn.nrmn.restapi.requestcache.RequestCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
@Tag(name = "divers")
public interface DiverRepository extends JpaRepository<Diver, Integer>, JpaSpecificationExecutor<Diver>, EntityCriteria<Diver> {

    @Override
    @Query("SELECT d FROM  Diver  d WHERE d.initials = :initials")
    @RequestCache
    List<Diver> findByCriteria(@Param("initials")String initials);


    @Override
    @RestResource
    Page<Diver> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends Diver> S save(S s);

    @Override
    @RestResource
    Optional<Diver> findById(Integer integer);
}
