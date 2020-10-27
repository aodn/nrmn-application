package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiverRepository extends JpaRepository<Diver, Integer>, JpaSpecificationExecutor<Diver> {
    List<Diver> findByInitials(String initials);
}
