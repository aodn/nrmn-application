package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.ErrorCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ErrorCheckRepository extends JpaRepository<ErrorCheck, Long>, JpaSpecificationExecutor<ErrorCheck> {
    @Modifying
    @Transactional
    @Query("delete  FROM ErrorCheck err WHERE err.id.jobId  = :jobId")
    Integer deleteWithFileID(@Param("jobId")String jobId);
}