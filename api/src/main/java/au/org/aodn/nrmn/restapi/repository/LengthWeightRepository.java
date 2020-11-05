package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.LengthWeight;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "length weights")
public interface LengthWeightRepository extends JpaRepository<LengthWeight, Integer>,
    JpaSpecificationExecutor<LengthWeight> {

    @Override
    @RestResource
    Page<LengthWeight> findAll(Pageable pageable);

    @Override
    @RestResource
    <S extends LengthWeight> S save(S s);

    @Override
    @RestResource
    Optional<LengthWeight> findById(Integer integer);
}
