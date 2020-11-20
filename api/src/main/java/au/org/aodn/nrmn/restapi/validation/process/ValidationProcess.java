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
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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


    public List<StagedRowError> process(StagedJob job) {
        val stagedRows = rowRepo.findRowsByReference(job.getReference());
        val program = job.getProgram();
        val preCheck =
                stagedRows.stream()
                        .map(row -> preProcess.validate(row).bimap(err -> err, value -> Seq.of(value)))
                        .reduce(
                                Validated.valid(Seq.empty()),
                                (v1, v2) -> v1.combine(Monoids.seqConcat(), v2));
        if (preCheck.isInvalid()) {
            return toErrorList(preCheck);
        }

        val rowValidations = preCheck.orElseGet(Seq::empty);

        val rowValidationHMap =
                rowValidations.map(seq -> seq.toHashMap(Tuple2::_1, Tuple2::_2));

        val formattedRows = rowValidationHMap.map(preProcess::toFormat).toList();
        //Todo adding formatted validation here;

        return Collections.emptyList();
    }
}
