package au.org.aodn.nrmn.restapi.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import au.org.aodn.nrmn.restapi.data.model.Program;
import io.swagger.v3.oas.annotations.tags.Tag;

@RepositoryRestResource
@Tag(name = "programs")
public interface ProgramRepository extends JpaRepository<Program, Integer>, JpaSpecificationExecutor<Program> {

    @Override
    @RestResource()
    Optional<Program> findById(Integer integer);

    @Query(value = "SELECT p from Program p where p.isActive = TRUE")
    List<Program> findActive();

    @Query(value = "SELECT p from Program p where p.id = 0")
    Program getNoneProgram();
}
