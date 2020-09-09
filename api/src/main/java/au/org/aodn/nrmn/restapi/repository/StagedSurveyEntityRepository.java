package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StagedSurveyEntityRepository extends JpaRepository<StagedSurveyEntity, Long>, JpaSpecificationExecutor<StagedSurveyEntity> {
    Optional<StagedSurveyEntity> findById(Long id);

    @Query("SELECT r FROM StagedSurveyEntity r where  r.stagedJob.id = :fileID")
    List<StagedSurveyEntity> findRawSurveyByFileID(@Param("fileID")String fileID);
}