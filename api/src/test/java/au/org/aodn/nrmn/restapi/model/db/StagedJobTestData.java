package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.StagedJob.StagedJobBuilder;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class StagedJobTestData {

    @Autowired
    private StagedJobRepository stagedJobRepository;

    @Autowired
    private SecUserTestData userTestData;

    @Autowired
    private ProgramTestData programTestData;

    public StagedJob persistedStagedJob() {
        val stagedJob = defaultBuilder().build();
        stagedJobRepository.saveAndFlush(stagedJob);
        return stagedJob;
    }

    public StagedJobBuilder defaultBuilder() {
        val program = programTestData.persistedProgram();
        val user = userTestData.persistedUser();
        return StagedJob.builder()
                        .program(program)
                        .reference("survey.xls")
                        .source(SourceJobType.INGEST)
                        .creator(user)
                        .status(StatusJobType.STAGED)
                        .isExtendedSize(false)
                        .rows(Collections.emptyList())
                        .logs(Collections.emptyList());
    }

    public StagedJob persistedJobWithReference(String reference) {
        val stagedJob = defaultBuilder()
                .reference(reference)
                .build();

        stagedJob.setRows(Collections.singletonList(
                StagedRow.builder()
                         .block("1")
                         .buddy("Nadia")
                         .code("nte")
                         .commonName("Blue-throat wrasse")
                         .date("12/12/2019")
                         .depth("6")
                         .direction("0")
                         .diver("SDL")
                         .inverts("0")
                         .stagedJob(stagedJob)
                         .build())
        );

        return stagedJobRepository.saveAndFlush(stagedJob);
    }

}
