package au.org.aodn.nrmn.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.db.model.audit.UserActionAudit;

@Repository
public interface UserActionAuditRepository extends JpaRepository<UserActionAudit, Long>,
 JpaSpecificationExecutor<UserActionAudit> {
}

