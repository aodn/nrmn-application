package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.StagedJob.StagedJobBuilder;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.StagedJobRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StagedJobTestData {

    @Autowired
    private StagedJobRepository stagedJobRepository;

    @Autowired
    private ProgramTestData programTestData;
    public StagedJob persistedStagedJob() {
        val stagedJob = defaultBuilder().build();
        return stagedJobRepository.saveAndFlush(stagedJob);
    }

    public StagedJobBuilder defaultBuilder() {
        val program = programTestData.persistedProgram();
        return StagedJob.builder()
                .program(program)
                .reference("survey.xls")
                .source(SourceJobType.FILE)
                .status(StatusJobType.STAGED);
    }
}
