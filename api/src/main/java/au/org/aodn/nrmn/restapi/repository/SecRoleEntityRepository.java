package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource()
public interface SecRoleEntityRepository  extends JpaRepository<SecRoleEntity, Integer>, JpaSpecificationExecutor<SecRoleEntity> {
}
