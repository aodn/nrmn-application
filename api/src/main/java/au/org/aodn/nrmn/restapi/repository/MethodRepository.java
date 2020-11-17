package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Method;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource
@Tag(name = "methods")
public interface MethodRepository extends JpaRepository<Method, Integer>, JpaSpecificationExecutor<Method> {
    @Override
    @RestResource
    Optional<Method> findById(Integer integer);

    @Override
    @RestResource
    Page<Method> findAll(Pageable pageable);
}
