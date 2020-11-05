package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedJobLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagedJobLogRepository extends JpaRepository<StagedJobLog, Long> {
}
