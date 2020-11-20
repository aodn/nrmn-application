package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationProcess extends ValidatorHelpers {


    @Autowired
    StagedRowRepository rowRepo;

    @Autowired
    StagedJobRepository jobRepo;
    @Autowired
    StagedRowErrorRepository errorRepo;

    @Autowired
    RawValidation preProcess;


    public List<StagedRowError> process(StagedJob job){
        val stagedRows = rowRepo.findRowByReference(job.getReference());
        val program = job.getProgram();
        val preCheck =
                stagedRows.stream().map(row ->  preProcess.validate(row)).reduce(
                Validated.valid(Seq.empty()),
                (v1, v2) -> v1.combine(Monoids.firstNonNull(), v2));
        if (preCheck.isInvalid()) {
            return toErrorList(preCheck);
        }
        val stagedRowFormatteds = preProcess.preValidated(stagedRows);
        return Collections.emptyList();

    }
}
