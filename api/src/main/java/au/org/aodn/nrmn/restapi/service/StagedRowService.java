package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
public class StagedRowService {

    @Autowired
    StagedRowRepository rowRepo;
    @Autowired
    StagedJobRepository jobRepo;

    @Transactional
    public Validated<ErrorInput, Integer> save(Long jobId, List<Long> toDeleteRowIds, List<StagedRow> newRows) {
        rowRepo.deleteAllByIds(toDeleteRowIds);
        Integer rowsAdded = 0;
        Optional<StagedJob> jobOptional = jobRepo.findById(jobId);
        if(jobOptional.isPresent() && newRows.size() > 0){
            newRows.forEach(row -> row.setStagedJob(jobOptional.get()));
            rowsAdded = rowRepo.saveAll(newRows).size();
        }
        return Validated.<ErrorInput, Integer>valid(rowsAdded);
    }
}
