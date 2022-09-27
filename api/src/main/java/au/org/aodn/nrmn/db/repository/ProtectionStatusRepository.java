package au.org.aodn.nrmn.db.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.MarineProtectedArea;
import au.org.aodn.nrmn.db.model.ProtectionStatus;

@RepositoryRestResource
@Tag(name = "protection statuses")
public interface ProtectionStatusRepository extends JpaRepository<ProtectionStatus, Integer>,
 JpaSpecificationExecutor<MarineProtectedArea> {

    @Override
    @RestResource
    Page<ProtectionStatus> findAll(Pageable pageable);
    
}
