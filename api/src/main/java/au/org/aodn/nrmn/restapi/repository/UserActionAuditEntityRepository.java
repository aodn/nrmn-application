package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionAuditEntityRepository  extends JpaRepository<UserActionAuditEntity, Long>, JpaSpecificationExecutor<UserActionAuditEntity> {}

