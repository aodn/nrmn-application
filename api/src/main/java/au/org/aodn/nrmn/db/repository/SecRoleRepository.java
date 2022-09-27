package au.org.aodn.nrmn.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.db.model.SecRole;
import au.org.aodn.nrmn.db.model.SecUser;
import au.org.aodn.nrmn.db.model.enums.SecRoleName;

import java.util.Optional;

@Repository
public interface SecRoleRepository extends JpaRepository<SecRole, String>, JpaSpecificationExecutor<SecUser> {
    Optional<SecRole> findByName(SecRoleName name);
}
