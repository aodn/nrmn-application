package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import au.org.aodn.nrmn.restapi.repository.model.SurveyMethodBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StagedSurveyRepository extends JpaRepository<StagedSurvey, Long>, JpaSpecificationExecutor<StagedSurvey> {
    Optional<StagedSurvey> findById(Long id);

    @Query("SELECT r FROM StagedSurvey r WHERE  r.stagedJob.id = :fileID")
    List<StagedSurvey> findRawSurveyByFileID(@Param("fileID") String fileID);

    @Query("SELECT r FROM StagedSurvey r  WHERE  r.stagedJob.id = :fileID GROUP BY r.siteNo, r.date, r.depth HAVING COUNT(r) > 1")
    List<StagedSurvey> findDuplicatesByFileID(@Param("fileID") String fileID);

    @Query("SELECT CONCAT(CONCAT(CONCAT(r.siteNo, r.date),r.depth), r.method) as id, r.method as method, r.block as block FROM StagedSurvey r  WHERE  r.stagedJob.id = :fileID AND r.method IN ('1','2') GROUP BY r.siteNo, r.date, r.depth, r.method, r.block")
    List<SurveyMethodBlock> findBlockMethods12(@Param("fileID") String fileID);
}