package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import au.org.aodn.nrmn.restapi.model.db.enums.SecRoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SecRoleEntityRepository extends JpaRepository<SecRoleEntity, String>, JpaSpecificationExecutor<SecUserEntity> {
  Optional<SecRoleEntity>  findByName(SecRoleName name);
}
