package au.org.aodn.nrmn.restapi.data.repository;

import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.Measure;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Integer>, JpaSpecificationExecutor<Measure> {

    @Query("SELECT m FROM Measure m WHERE m.measureType.measureTypeId = :measureTypeId and m.seqNo = :seqNo")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Optional<Measure> findByMeasureTypeIdAndSeqNo(@Param("measureTypeId") Integer measureTypeId,
                                                  @Param("seqNo") Integer seqNo);

}
