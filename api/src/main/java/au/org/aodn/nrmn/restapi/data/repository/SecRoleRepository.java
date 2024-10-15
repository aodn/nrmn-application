package au.org.aodn.nrmn.restapi.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.SecRole;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.enums.SecRoleName;

import javax.persistence.QueryHint;
import java.util.Optional;

@Repository
public interface SecRoleRepository extends JpaRepository<SecRole, String>, JpaSpecificationExecutor<SecUser> {
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Optional<SecRole> findByName(SecRoleName name);
}
