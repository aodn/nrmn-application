package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.MeasureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureTypeRepository extends JpaRepository<MeasureType, Integer>,
 JpaSpecificationExecutor<MeasureType> {

}
