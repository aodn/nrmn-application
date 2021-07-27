package au.org.aodn.nrmn.restapi.validation.validators.global;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang.SerializationUtils;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import au.org.aodn.nrmn.restapi.validation.process.ValidationProcess;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@WithTestData
class ATRCMethodCheckIT {

    @Autowired
    ValidationProcess validationProcess;

    @Test
    void only12methodShouldSucceed() {
        String date = "11/09/2020";
        String depth = "7";
        String siteNo = "ERZ1";

        StagedRow m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);

        StagedRow m2b1 = (StagedRow) SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        StagedRow m2b2 = (StagedRow) SerializationUtils.clone(m2b1);
        m2b1.setBlock("2");

        StagedRow m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        StagedRow m2b1d8 = (StagedRow) SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");

        Collection<ValidationError> res = validationProcess.checkFormatting("ATRC", false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(m1b1, m2b1, m2b2, m1b1d8, m2b1d8));

        assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }


    @Test
    void onlyMethod0345ShouldSucceed() {

        String date = "11/09/2020";
        String depth = "7";
        String siteNo = "ERZ1";

        StagedRow m0b1 = new StagedRow();
        m0b1.setMethod("0");
        m0b1.setBlock("1");
        m0b1.setDate(date);
        m0b1.setDepth(depth);
        m0b1.setSiteCode(siteNo);

        StagedRow m0b2 = (StagedRow) SerializationUtils.clone(m0b1);
        m0b2.setBlock("2");

        StagedRow m3b1 = (StagedRow) SerializationUtils.clone(m0b1);
        m3b1.setMethod("3");

        StagedRow m3b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m3b3.setBlock("3");

        StagedRow m5b3 = (StagedRow) SerializationUtils.clone(m3b1);
        m5b3.setBlock("5");

        Collection<ValidationError> res = validationProcess.checkFormatting("ATRC", false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(m0b1, m0b2, m3b1, m3b3, m5b3));

        assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }

    @Test
    void missingM2ShouldFail() {
        String date = "11/09/2020";
        String depth = "7";
        String siteNo = "ERZ1";

        StagedRow m1b1 = new StagedRow();
        m1b1.setMethod("1");
        m1b1.setBlock("1");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);

        StagedRow m1b1d10 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d10.setDepth("10");
        StagedRow m1b1d8 = (StagedRow) SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        StagedRow m2b1d8 = (StagedRow) SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");

        Collection<ValidationError> res = validationProcess.checkFormatting("ATRC", false, Arrays.asList("ERZ1"), Arrays.asList(), Arrays.asList(m1b1, m1b1d10, m1b1d8, m2b1d8));

        assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }
}
