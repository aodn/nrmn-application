package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "staged job log")
public interface StagedJobLogRepository extends JpaRepository<StagedJobLog, Long> {
    @Override
    Page<StagedJobLog> findAll(Pageable pageable);

    @Override
    Optional<StagedJobLog> findById(Long aLong);
}
