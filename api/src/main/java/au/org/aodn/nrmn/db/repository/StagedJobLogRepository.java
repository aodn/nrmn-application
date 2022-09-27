package au.org.aodn.nrmn.db.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.StagedJobLog;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "staged job log")
public interface StagedJobLogRepository extends JpaRepository<StagedJobLog, Long> {
    @Override
    @RestResource
    Page<StagedJobLog> findAll(Pageable pageable);

    @Override
    @RestResource
    Optional<StagedJobLog> findById(Long aLong);
}
