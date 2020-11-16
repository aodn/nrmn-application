package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.ObsItemType;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObsItemTypeRepository extends JpaRepository<ObsItemType, Integer>, JpaSpecificationExecutor<ObsItemType>, EntityCriteria<ObsItemType> {

    @Override
    @Query("SELECT o FROM  ObsItemType  o WHERE o.obsItemTypeName = :obsItemName")
    Optional<ObsItemType> findByCriteria(@Param("obsItemName")String obsItemName);
}
