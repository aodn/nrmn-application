package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObsItemTypeRepository extends JpaRepository<ObsItemType, Integer>,
 JpaSpecificationExecutor<ObsItemType> {
    List<ObsItemType> findByObsItemTypeName(String obsItemName);
}
