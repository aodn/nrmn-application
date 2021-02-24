package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import javax.persistence.QueryHint;
import org.springframework.data.repository.query.Param;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@RepositoryRestResource
@Tag(name = "divers")
public interface DiverRepository
        extends JpaRepository<Diver, Integer>, JpaSpecificationExecutor<Diver>, EntityCriteria<Diver> {

    @Override
    @Query("SELECT d FROM Diver d WHERE d.initials = :initials")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    List<Diver> findByCriteria(@Param("initials") String initials);

    @Override
    @RestResource
    @Query(value = "SELECT * FROM {h-schema}diver_ref d ORDER BY (CASE WHEN initials SIMILAR TO '%[a-zA-Z]' THEN 0 ELSE 1 END), d.initials", 
           countQuery = "SELECT count(*) FROM {h-schema}diver_ref",
           nativeQuery = true)
    Page<Diver> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends Diver> S save(S s);

    @Override
    @RestResource
    Optional<Diver> findById(Integer integer);
}
