package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.dto.stage.RowErrors;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowErrorRepository;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import au.org.aodn.nrmn.restapi.validation.model.MonoidRowValidation;
import au.org.aodn.nrmn.restapi.validation.summary.DefaultSummary;
import cyclops.companion.Monoids;
import cyclops.data.ImmutableList;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
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
    FormattedValidation postProcess;
    @Autowired
    GlobalValidation globalProcess;

    @Autowired
    RawValidation preProcess;

    @Autowired
    DefaultSummary summary;

    public ValidationResponse process(StagedJob job) {
        val rawValidators = preProcess.getRawValidators(job);
        val reducer = new MonoidRowValidation<Seq<Tuple2<String, Object>>>(Seq.empty(), Monoids.<Tuple2<String, Object>>seqConcat());
        val rowChecks =
                job.getRows().stream()
                        .map(row -> preProcess.validate(row, rawValidators))
                        .collect(Collectors.toList());
        val preCheck = rowChecks.stream().reduce(reducer.zero(), reducer::apply);

        if (preCheck.getValid().isInvalid()) {
            val errors =
                    preCheck
                            .getRows()
                            .flatMap(row ->
                                    Seq.fromIterable(row.getErrors())
                            ).toList();

            val msgSummary = summary.aggregate(errors);
            return new ValidationResponse(
                    job,
                    preCheck.getRows().map(row -> new RowErrors(row.getId(),row.getErrors())).toList(),
                    msgSummary,
                    Collections.emptyList(),
                    Collections.emptyList());
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
                job,
                Collections.emptyList(),
                Collections.emptyList(),
                toErrorList(globalResult),
                Collections.emptyList());
    }
}
