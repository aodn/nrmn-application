package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.SpeciesWithAttributes;
import au.org.aodn.nrmn.restapi.repository.model.EntityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesWithAttributesRepository extends JpaRepository<SpeciesWithAttributes, Long>, JpaSpecificationExecutor<SpeciesWithAttributes> {
}
