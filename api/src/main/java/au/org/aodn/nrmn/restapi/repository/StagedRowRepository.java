package au.org.aodn.nrmn.restapi.repository;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StagedRowRepository extends JpaRepository<StagedRow, Long>, JpaSpecificationExecutor<StagedRow> {
    Optional<StagedRow> findById(Long id);

    @Query("SELECT r FROM StagedRow r where  r.stagedJob.id = :fileID")
    List<StagedRow> findRawSurveyByFileID(@Param("fileID")String fileID);
}