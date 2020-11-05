package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecRole;
import au.org.aodn.nrmn.restapi.model.db.SecUser;
import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SecRoleRepository extends JpaRepository<SecRole, String>, JpaSpecificationExecutor<SecUser> {
    Optional<SecRole> findByName(SecRoleName name);
}
