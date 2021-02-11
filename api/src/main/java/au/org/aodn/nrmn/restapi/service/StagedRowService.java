package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import cyclops.control.Validated;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StagedRowService {

    @Autowired
    StagedRowRepository rowRepo;
    @Autowired
    StagedJobRepository jobRepo;

    public Validated<ErrorInput, Integer> update(Long jobId, List<StagedRow> newRows) {
        return jobRepo.findById(jobId).map(job -> {
            newRows.forEach(row -> row.setStagedJob(job));
            val saved = rowRepo.saveAll(newRows);
            return Validated.<ErrorInput, Integer>valid(saved.size());
        }).orElseGet(() ->
                Validated.invalid(new ErrorInput("Couldn't find job: " + jobId, "job")));

    }
}
