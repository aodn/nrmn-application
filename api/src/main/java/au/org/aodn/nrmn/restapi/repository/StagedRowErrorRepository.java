package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StagedRowErrorRepository extends JpaRepository<StagedRowError, Long>, JpaSpecificationExecutor<StagedRowError> {
    @Modifying
    @Transactional
    @Query("delete  FROM StagedRowError err WHERE err.id.jobId  = :jobId")
    Integer deleteWithJobId(@Param("jobId")long jobId);
}