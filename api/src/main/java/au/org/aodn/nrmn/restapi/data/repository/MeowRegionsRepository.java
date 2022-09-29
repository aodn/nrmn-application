package au.org.aodn.nrmn.restapi.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import au.org.aodn.nrmn.restapi.data.model.MeowEcoRegions;

public interface MeowRegionsRepository extends JpaRepository<MeowEcoRegions, Integer>, JpaSpecificationExecutor<MeowEcoRegions> {
}
