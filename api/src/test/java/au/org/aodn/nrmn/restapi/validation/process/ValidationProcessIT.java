package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.StagedJobTestData;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class ValidationProcessIT {

    @Autowired
    private ValidationProcess validationProcess;

    @Autowired
    private StagedJobTestData stagedJobTestData;

    @Test
    public void testValidationResultsNotDuplicated() {
        val stagedJob1 = stagedJobTestData.persistedJobWithReference("ref1.xls");
        val stagedJob2 = stagedJobTestData.persistedJobWithReference("ref1.xls");

        val response = validationProcess.process(stagedJob2);

        assertEquals("Validation rows size does not equal job rows size", stagedJob2.getRows().size(),
                response.getRows().size());

    }

}
