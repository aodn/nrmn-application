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
    public Validated<ErrorInput, Integer> save(Long jobId, List<Long> toDeleteRowIds, List<StagedRow> toAddUpdateRows) {
        rowRepo.deleteAllByIds(toDeleteRowIds);
        Integer rowsSaved = 0;
        Optional<StagedJob> jobOptional = jobRepo.findById(jobId);
        if(jobOptional.isPresent() && toAddUpdateRows.size() > 0){
            toAddUpdateRows.forEach(row -> row.setStagedJob(jobOptional.get()));
            rowsSaved = rowRepo.saveAll(toAddUpdateRows).size();
        }
        return Validated.<ErrorInput, Integer>valid(rowsSaved);
    }
}
