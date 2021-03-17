package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ReportGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
@Tag(name = "report groups")
public interface ReportGroupRepository extends JpaRepository<ReportGroup, Integer>,
 JpaSpecificationExecutor<ReportGroup> {

    @Override
    @RestResource
    Page<ReportGroup> findAll(Pageable pageable);

}
