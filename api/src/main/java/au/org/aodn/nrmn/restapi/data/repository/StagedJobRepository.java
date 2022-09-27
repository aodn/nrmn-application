package au.org.aodn.nrmn.restapi.data.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.repository.projections.StagedJobList;

import java.util.Optional;

@RepositoryRestResource(excerptProjection = StagedJobList.class)
@Tag(name = "staged jobs")
public interface StagedJobRepository extends JpaRepository<StagedJob, Long> {
    Optional<StagedJob> findByReference(String reference);

    @Override
    @RestResource
    Optional<StagedJob> findById(Long stagedJobId);

    @Override
    @RestResource
    Page<StagedJob> findAll(Pageable pageable);

}
