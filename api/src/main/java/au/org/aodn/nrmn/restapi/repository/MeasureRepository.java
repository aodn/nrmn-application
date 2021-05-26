package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.Measure;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Optional;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

@Repository
public interface MeasureRepository extends JpaRepository<Measure, Integer>, JpaSpecificationExecutor<Measure> {

    @Query("SELECT m FROM Measure m WHERE m.measureType.measureTypeId = :measureTypeId and m.seqNo = :seqNo")
    @Cacheable("measures")
    Optional<Measure> findByMeasureTypeIdAndSeqNo(@Param("measureTypeId") Integer measureTypeId,
                                                  @Param("seqNo") Integer seqNo);

}
