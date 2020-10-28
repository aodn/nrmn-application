package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StagedJobRepository extends JpaRepository<StagedJob, Long> {
    Optional<StagedJob> findByReference(String reference);
}
