package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Integer>,
        JpaSpecificationExecutor<Observation> {

    @Query(nativeQuery = true, value = "SELECT * FROM nrmn.UiSpeciesAttributes usa  where usa.id = :id")
    List<UiSpeciesAttributes> getSpeciesAttributesById(@Param("id") Integer id);

}
