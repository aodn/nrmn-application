package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StagedJobEntityRepository extends JpaRepository<StagedJobEntity, String> {
    Optional<StagedJobEntity> findById(String jobID);
}
