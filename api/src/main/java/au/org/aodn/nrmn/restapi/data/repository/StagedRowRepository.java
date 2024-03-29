package au.org.aodn.nrmn.restapi.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.model.RowMethodBlock;
import au.org.aodn.nrmn.restapi.data.repository.model.StagedSurveyMethod;
import au.org.aodn.nrmn.restapi.data.repository.model.StagedSurveyTransect;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;


@Repository
public interface StagedRowRepository extends JpaRepository<StagedRow, Long>, JpaSpecificationExecutor<StagedRow> {
    Optional<StagedRow> findById(Long id);

    @Query("SELECT r FROM StagedRow r WHERE  r.stagedJob.id = :jobId")
    List<StagedRow> findRowsByJobId(@Param("jobId") Long jobId);

    @Query("SELECT r FROM StagedRow r  WHERE  r.stagedJob.id = :id GROUP BY r.siteCode, r.date, r.depth HAVING COUNT(r) > 1")
    List<StagedRow> findDuplicatesByReference(@Param("id") Long id);

    @Query("SELECT CONCAT(CONCAT(CONCAT(r.siteCode, r.date),r.depth), r.method) as id, r.method as method, r.block as block FROM StagedRow r  WHERE  r.stagedJob.id = :id AND r.method IN ('1','2') GROUP BY r.siteCode, r.date, r.depth, r.method, r.block")
    List<RowMethodBlock> findBlockMethods12(@Param("id") Long id);

    @Query("SELECT r.siteCode as siteCode, r.date as date, r.depth as depth, r.method as method " +
            "FROM StagedRow r  " +
            "WHERE r.stagedJob.id = :id " +
            "GROUP BY r.siteCode, r.date, r.depth, r.method ")
    List<StagedSurveyMethod> getStagedSurveyMethods(@Param("id") Long id);

    @Query("SELECT NEW au.org.aodn.nrmn.restapi.data.repository.model.StagedSurveyTransect(r.siteCode, r.date, r.depth, r.method, r.block) " +
            "FROM StagedRow r " +
            "WHERE r.stagedJob.id = :id " +
            "GROUP BY r.siteCode, r.date, r.depth, r.method, r.block")
    List<StagedSurveyTransect> getStagedSurveyTransects(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("delete from StagedRow r where r.id in ?1")
    void deleteAllByIds(@Param("ids") List<Long> ids);
}
