package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import au.org.aodn.nrmn.restapi.validation.model.MonoidRowValidation;
import cyclops.companion.Monoids;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
    FormattedValidation postProcess;
    @Autowired
    GlobalValidation globalProcess;

    @Autowired
    RawValidation preProcess;


    public ValidationResponse process(StagedJob job) {
        val stagedRows = rowRepo.findRowsByReference(job.getReference());
        val rawValidators = preProcess.getRawValidators(job);
        val reducer = new MonoidRowValidation<Seq<Tuple2<String, Object>>>(Seq.empty(), Monoids.<Tuple2<String, Object>>seqConcat());
        val rowChecks =
                stagedRows.stream()
                        .map(row -> preProcess.validate(row, rawValidators))
                        .collect(Collectors.toList());
        val preCheck = rowChecks.stream().reduce(reducer.zero(), reducer::apply);

        if (preCheck.getValid().isInvalid()) {
            return new ValidationResponse(preCheck.getRows().toList(), Collections.emptyList(), Collections.emptyList());
        }

        val rowWithHasMap = rowChecks
                .stream()
                .flatMap(
                        withValidation -> withValidation.getValid()
                                .map(seq -> seq.toHashMap(Tuple2::_1, Tuple2::_2)).stream())
                .collect(Collectors.toList());


        val formattedRows = rowWithHasMap.stream().map(preProcess::toFormat).collect(Collectors.toList());

        val formattedResult = postProcess.process(formattedRows, job);
        val globalResult = globalProcess.process(job);
        return new ValidationResponse(
                formattedResult.getRows().toList(),
                toErrorList(globalResult),
                Collections.emptyList());
    }
}
