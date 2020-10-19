package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.AphiaRelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AphiaRelTypeRepository extends JpaRepository<AphiaRelType, Integer>,
 JpaSpecificationExecutor<AphiaRelType> {

}
