package au.org.aodn.nrmn.restapi.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.SecUser;
import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.model.db.ProgramTestData;
import au.org.aodn.nrmn.restapi.model.db.SecUserTestData;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.ValidationProcess;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import au.org.aodn.nrmn.restapi.validation.process.FormattedTestProvider;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@Transactional
@WithNoData
public class ValidationProcessIT extends FormattedTestProvider {

    @Autowired
    protected DataValidation dataValidation;

    @Autowired
    protected ValidationProcess validationProcess;

    @Autowired
    protected ProgramTestData ptd;

    @Autowired
    protected SecUserTestData utd;

    private final String date = "11/09/2020";
    private final String depth = "7";

    @Test
    public void blockingWhenDataMissing() {
        Program program = ptd.persistedProgram();
        SecUser user = utd.persistedUser();
        StagedJob stagedJob = StagedJob.builder()
                .program(program)
                .reference("survey.xls")
                .source(SourceJobType.INGEST)
                .creator(user)
                .status(StatusJobType.STAGED)
                .reference("ref1.xls")
                .isExtendedSize(false)
                .rows(Collections.emptyList())
                .logs(Collections.emptyList())
                .build();

        ValidationResponse errors = validationProcess.process(stagedJob);

        assertTrue(errors.getErrors().stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Survey data is missing") && e.getLevelId() == ValidationLevel.BLOCKING));
    }

    @Test
    public void siteCodeMatchesWithSameCase() {
        StagedRow row = StagedRow.builder()
                .siteCode("ERZ1")
                .method("1")
                .block("1")
                .date(date)
                .depth(depth)
                .build();

        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true,
                Collections.singletonList("erz1"), Collections.emptyList(), Collections.singletonList(row));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Site Code does not exist")));
    }

    @Test
    public void siteCodeMatchesWithDifferentCase() {
        StagedRow row = StagedRow.builder()
                .siteCode("Erz1")
                .method("1")
                .block("1")
                .date(date)
                .depth(depth)
                .build();
        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true,
                Collections.singletonList("erz1"), Collections.emptyList(), Collections.singletonList(row));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Site Code does not exist")));
    }

    @Test
    public void siteCodeMatchesWithInvalidSiteCode() {
        StagedRow row = StagedRow.builder()
                .siteCode("AbC1")
                .method("1")
                .block("1")
                .date(date)
                .depth(depth)
                .build();
        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true,
                Collections.singletonList("erz1"), Collections.emptyList(), Collections.singletonList(row));

        Assertions.assertTrue(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("Site Code does not exist")));
    }

}
