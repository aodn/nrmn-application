package au.org.aodn.nrmn.restapi.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.db.model.StagedJob;
import au.org.aodn.nrmn.db.model.StagedRow;
import au.org.aodn.nrmn.db.repository.StagedJobRepository;
import au.org.aodn.nrmn.db.repository.StagedRowRepository;

@Service
public class StagedRowService {

    @Autowired
    StagedRowRepository rowRepo;
    @Autowired
    StagedJobRepository jobRepo;

    @Transactional
    public Boolean save(Long jobId, List<Long> toDeleteRowIds, List<StagedRow> toAddUpdateRows) {
        rowRepo.deleteAllByIds(toDeleteRowIds);
        Integer rowsSaved = 0;
        Optional<StagedJob> jobOptional = jobRepo.findById(jobId);
        if(jobOptional.isPresent() && toAddUpdateRows.size() > 0){
            toAddUpdateRows.forEach(row -> row.setStagedJob(jobOptional.get()));
            rowsSaved = rowRepo.saveAll(toAddUpdateRows).size();
        }
        return toAddUpdateRows.size() == rowsSaved;
    }
}
