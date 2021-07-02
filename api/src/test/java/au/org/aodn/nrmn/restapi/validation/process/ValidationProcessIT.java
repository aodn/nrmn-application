package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithNoData;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.SerializationUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithNoData
public class ValidationProcessIT {

    @Autowired
    private ValidationProcess validationProcess;

    @Autowired
    private ProgramTestData ptd;

    @Autowired
    private SecUserTestData utd;

    @Test
    public void testValidationResultsNotDuplicated() {
        val program = ptd.persistedProgram();
        val user = utd.persistedUser();
        val stagedJob = StagedJob.builder()
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

        val row1 = StagedRow.builder()
                .block("1")
                .method("1")
                .buddy("Row1")
                .code("nte")
                .commonName("Blue-throat wrasse")
                .date("12/12/2019")
                .depth("6")
                .species("Species 56")
                .direction("0")
                .diver("SDL")
                .inverts("0")
                .stagedJob(stagedJob)
                .build();

        val row2 = (StagedRow) SerializationUtils.clone(row1);
        row2.setMethod("2");


        val row3 = (StagedRow) SerializationUtils.clone(row1);
        row3.setBlock("2");

        val row4 = (StagedRow) SerializationUtils.clone(row2);
        row4.setBlock("2");

        val measures = new HashMap<Integer, String>() {{
            put(1, "3");
            put(2, "4");
        }};
        row1.setMeasureJson(measures);
        row2.setMeasureJson(measures);
        row3.setMeasureJson(measures);
        row4.setMeasureJson(measures);

        stagedJob.setRows(Arrays.asList(row1, row2, row3, row4));


        val response = validationProcess.process(stagedJob);

        assertEquals("Validation rows size does not equal job rows size", stagedJob.getRows().size(),
                response.getErrors().size());

    }

}
