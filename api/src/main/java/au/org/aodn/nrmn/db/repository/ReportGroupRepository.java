package au.org.aodn.nrmn.db.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.db.model.ReportGroup;

@RepositoryRestResource
@Tag(name = "report groups")
public interface ReportGroupRepository extends JpaRepository<ReportGroup, Integer>,
 JpaSpecificationExecutor<ReportGroup> {

    @Override
    @RestResource
    Page<ReportGroup> findAll(Pageable pageable);

}
