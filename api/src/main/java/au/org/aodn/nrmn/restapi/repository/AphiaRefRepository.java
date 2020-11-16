package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AphiaRefRepository extends JpaRepository<AphiaRef, Integer>, JpaSpecificationExecutor<AphiaRef>, EntityCriteria<AphiaRef> {
    @Override
    @Query("SELECT a from AphiaRef  a WHERE a.validName = :name")
    Optional<AphiaRef> findByCriteria(@Param("name") String name);
}
