package au.org.aodn.nrmn.restapi.repository;

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
public interface DiverRepository extends JpaRepository<Diver, Integer>, JpaSpecificationExecutor<Diver>, EntityCriteria<Diver> {
    List<Diver> findByInitials(String initials);

    @Override
    @Query("SELECT d FROM  Diver  d WHERE d.initials = :initials")
    Optional<Diver> findByCriteria(@Param("initials")String initials);
}
