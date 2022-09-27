package au.org.aodn.nrmn.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import au.org.aodn.nrmn.db.model.MeowEcoRegions;

public interface MeowRegionsRepository extends JpaRepository<MeowEcoRegions, Integer>, JpaSpecificationExecutor<MeowEcoRegions> {
}
