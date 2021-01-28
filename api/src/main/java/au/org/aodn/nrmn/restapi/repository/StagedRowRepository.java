package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.model.RowMethodBlock;
import au.org.aodn.nrmn.restapi.repository.model.RowMethodCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StagedRowRepository extends JpaRepository<StagedRow, Long>, JpaSpecificationExecutor<StagedRow> {
    Optional<StagedRow> findById(Long id);

    @Query("SELECT r FROM StagedRow r WHERE  r.stagedJob.reference = :reference")
    List<StagedRow> findRowsByReference(@Param("reference") String ref);

    @Query("SELECT r FROM StagedRow r WHERE  r.stagedJob.id = :jobId")
    List<StagedRow> findRowsByJobId(@Param("jobId") Long jobId);

    @Query("SELECT r FROM StagedRow r  WHERE  r.stagedJob.id = :id GROUP BY r.siteCode, r.date, r.depth HAVING COUNT(r) > 1")
    List<StagedRow> findDuplicatesByReference(@Param("id") Long id);

    @Query("SELECT CONCAT(CONCAT(CONCAT(r.siteCode, r.date),r.depth), r.method) as id, r.method as method, r.block as block FROM StagedRow r  WHERE  r.stagedJob.id = :id AND r.method IN ('1','2') GROUP BY r.siteCode, r.date, r.depth, r.method, r.block")
    List<RowMethodBlock> findBlockMethods12(@Param("id") Long id);

    @Query("SELECT CONCAT(CONCAT(r.siteCode, r.date),r.depth) as id, r.method as method FROM StagedRow r  WHERE  r.stagedJob.id = :id GROUP BY r.siteCode, r.date, r.depth, r.method ")
    List<RowMethodBlock> getMethodsPerSurvey(@Param("id") Long  id);
}
