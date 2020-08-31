package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RawSurveyEntityRepository extends JpaRepository<StagedSurveyEntity, Long>, JpaSpecificationExecutor<StagedSurveyEntity> {
    List<StagedSurveyEntity> findByRid(RawSurveyID rid);

    @Query("SELECT r.rid.fileID FROM StagedSurveyEntity r group by  r.rid.fileID")
    List<String> getFileLIst();

    @Query("SELECT r FROM StagedSurveyEntity r where  r.rid.fileID = :fileID")
    List<StagedSurveyEntity> findRawSurveyByFileID(@Param("fileID")String fileID);
}