package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ErrorCheckEntityRepository extends JpaRepository<ErrorCheckEntity, Long>, JpaSpecificationExecutor<ErrorCheckEntity> {
    @Modifying
    @Transactional
    @Query("delete  FROM ErrorCheckEntity r WHERE  r.rawSurveyEntity.rid.fileID = :fileid")
    Integer deleteWithFileID(@Param("fileid")String fileID);
}