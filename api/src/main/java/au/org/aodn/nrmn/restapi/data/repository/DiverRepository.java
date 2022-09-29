package au.org.aodn.nrmn.restapi.data.repository;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.repository.model.EntityCriteria;
import io.swagger.v3.oas.annotations.tags.Tag;

@RepositoryRestResource
@Tag(name = "divers")
public interface DiverRepository
        extends JpaRepository<Diver, Integer>, JpaSpecificationExecutor<Diver>, EntityCriteria<Diver> {

    @Override
    @Query("SELECT d FROM Diver d WHERE lower(d.initials) = lower(:initials) or  lower(d.fullName) = lower(:initials)")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    List<Diver> findByCriteria(@Param("initials") String initials);

    @Query("SELECT d FROM Diver d WHERE lower(d.initials) = lower(:initials)")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    List<Diver> findByInitials(@Param("initials") String initials);

    @Query("SELECT d FROM Diver d WHERE lower(d.fullName) = lower(:fullName)")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    List<Diver> findByFullName(@Param("fullName") String fullName);

    @Override
    @RestResource
    @Query(value = "SELECT * FROM {h-schema}diver_ref d ORDER BY (CASE WHEN initials SIMILAR TO '%[a-zA-Z]' THEN 0 ELSE 1 END), LOWER(d.initials)", countQuery = "SELECT count(*) FROM {h-schema}diver_ref", nativeQuery = true)
    List<Diver> findAll();

    @Query("SELECT d FROM Diver d")
    @QueryHints({ @QueryHint(name = HINT_CACHEABLE, value = "true") })
    Collection<Diver> getAll();

    @Override
    <S extends Diver> S save(S s);

    @Override
    @RestResource
    Optional<Diver> findById(Integer integer);
}
