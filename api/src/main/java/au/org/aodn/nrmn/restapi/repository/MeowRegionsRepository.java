package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.MeowEcoRegions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MeowRegionsRepository extends JpaRepository<MeowEcoRegions, Integer>, JpaSpecificationExecutor<MeowEcoRegions> {
}
