package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SecRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SecRoleRepository extends JpaRepository<SecRoleEntity, Long> {
    @Query(value = "SELECT s FROM SecRoleEntity s WHERE CAST(s.name AS text) = :secRoleName")
    Optional<SecRoleEntity> findByName(@Param("secRoleName") String secRoleName);
}
