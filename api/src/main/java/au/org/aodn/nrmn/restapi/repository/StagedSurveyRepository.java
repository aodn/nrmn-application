package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StagedSurveyRepository extends JpaRepository<StagedSurvey, Long>, JpaSpecificationExecutor<StagedSurvey> {
    Optional<StagedSurvey> findById(Long id);

    @Query("SELECT r FROM StagedSurvey r where  r.stagedJob.id = :fileID")
    List<StagedSurvey> findRawSurveyByFileID(@Param("fileID")String fileID);
}