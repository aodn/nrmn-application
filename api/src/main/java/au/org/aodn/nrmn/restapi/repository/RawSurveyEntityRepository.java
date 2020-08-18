package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.RawSurveyEntity;
import au.org.aodn.nrmn.restapi.model.db.composedID.RawSurveyID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RawSurveyEntityRepository extends JpaRepository<RawSurveyEntity, Long>, JpaSpecificationExecutor<RawSurveyEntity> {
    List<RawSurveyEntity> findByRid(RawSurveyID rid);

    @Query("SELECT r.rid.fileID FROM RawSurveyEntity r group by  r.rid.fileID")
    List<String> getFileLIst();

    @Query("SELECT r FROM RawSurveyEntity r where  r.rid.fileID = :fileID")
    List<RawSurveyEntity> findRawSurveyByFileID(@Param("fileID")String fileID);
}