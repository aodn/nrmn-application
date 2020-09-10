package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.LocationRefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
* Generated by Spring Data Generator on 10/01/2020
*/
@Repository
@RepositoryRestResource
public interface LocationRefEntityRepository extends JpaRepository<LocationRefEntity, Integer>, JpaSpecificationExecutor<LocationRefEntity> {

}
