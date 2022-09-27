package au.org.aodn.nrmn.db.repository;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.db.model.Measure;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Integer>, JpaSpecificationExecutor<Measure> {

    @Query("SELECT m FROM Measure m WHERE m.measureType.measureTypeId = :measureTypeId and m.seqNo = :seqNo")
    @Cacheable("measures")
    Optional<Measure> findByMeasureTypeIdAndSeqNo(@Param("measureTypeId") Integer measureTypeId,
                                                  @Param("seqNo") Integer seqNo);

}
