package au.org.aodn.nrmn.restapi.model.db;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.model.StagedJob.StagedJobBuilder;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;

@Component
public class StagedJobTestData {

    @Autowired
    private StagedJobRepository stagedJobRepository;

    @Autowired
    private SecUserTestData userTestData;

    @Autowired
    private ProgramTestData programTestData;

    public StagedJob persistedStagedJob() {
        StagedJob stagedJob = defaultBuilder().build();
        stagedJobRepository.saveAndFlush(stagedJob);
        return stagedJob;
    }

    public StagedJobBuilder defaultBuilder() {
        Program program = programTestData.persistedProgram();
        SecUser user = userTestData.persistedUser();
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
        StagedJob stagedJob = defaultBuilder()
                .reference(reference)
                .build();

                StagedRow row1 = StagedRow.builder()
                        .block("1")
                        .method("1")
                        .buddy("Nadia")
                        .code("nte")
                        .commonName("Blue-throat wrasse")
                        .date("12/12/2019")
                        .depth("6")
                        .direction("0")
                        .diver("SDL")
                        .inverts("0")
                        .stagedJob(stagedJob)
                        .build();

                stagedJob.setRows(Collections.singletonList(row1)
        );

        return stagedJobRepository.saveAndFlush(stagedJob);
    }

}
