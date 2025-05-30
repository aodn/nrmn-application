package au.org.aodn.nrmn.restapi.integration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;

import javax.transaction.Transactional;

@Testcontainers
@SpringBootTest
@ExtendWith(PostgresqlContainerExtension.class)
@Transactional
@WithTestData
class ATRCMethodCheckIT {

    @Autowired
    DataValidation dataValidation;

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

        StagedRow m2b1 = SerializationUtils.clone(m1b1);
        m2b1.setMethod("2");
        StagedRow m2b2 = SerializationUtils.clone(m2b1);
        m2b1.setBlock("2");

        StagedRow m1b1d8 = SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        StagedRow m2b1d8 = SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");

        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"),
                List.of(), Arrays.asList(m1b1, m2b1, m2b2, m1b1d8, m2b1d8));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }

    @Test
    void method11OnIngestShouldFail() {
        var date = "11/09/2020";
        var depth = "7";
        var siteNo = "ERZ1";

        var m1b1 = new StagedRow();
        m1b1.setMethod("11");
        m1b1.setBlock("0");
        m1b1.setDate(date);
        m1b1.setDepth(depth);
        m1b1.setSiteCode(siteNo);
        m1b1.setSpecies("Haliotis rubra");

        var m2b1 = SerializationUtils.clone(m1b1);
        var m2b2 = SerializationUtils.clone(m2b1);
        var m1b1d8 = SerializationUtils.clone(m1b1);
        var m2b1d8 = SerializationUtils.clone(m1b1d8);

        m2b1.setMethod("2");
        m2b1.setBlock("2");
        m1b1d8.setDepth("8");
        m1b1d8.setMethod("2");

        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"),
                List.of(), Arrays.asList(m1b1, m2b1, m2b2, m1b1d8, m2b1d8));

        Assertions.assertTrue(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }

    @Test
    void method11OnCorrectionsShouldSucceed() {
        var date = "11/09/2020";
        var depth = "7";
        var siteNo = "ERZ1";

        var row = new StagedRow();
        row.setMethod("11");
        row.setBlock("0");
        row.setDate(date);
        row.setDepth(depth);
        row.setSiteCode(siteNo);
        row.setSpecies("Haliotis rubra");

        var res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, false, List.of("ERZ1"),
                List.of(), List.of(row));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
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

        StagedRow m0b2 = SerializationUtils.clone(m0b1);
        m0b2.setBlock("2");

        StagedRow m3b1 = SerializationUtils.clone(m0b1);
        m3b1.setMethod("3");

        StagedRow m3b3 = SerializationUtils.clone(m3b1);
        m3b3.setBlock("3");

        StagedRow m5b3 = SerializationUtils.clone(m3b1);
        m5b3.setBlock("5");

        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"),
                List.of(), Arrays.asList(m0b1, m0b2, m3b1, m3b3, m5b3));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
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

        StagedRow m1b1d10 = SerializationUtils.clone(m1b1);
        m1b1d10.setDepth("10");
        StagedRow m1b1d8 = SerializationUtils.clone(m1b1);
        m1b1d8.setDepth("8");
        StagedRow m2b1d8 = SerializationUtils.clone(m1b1d8);
        m1b1d8.setMethod("2");

        Collection<SurveyValidationError> res = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, List.of("ERZ1"),
                List.of(), Arrays.asList(m1b1, m1b1d10, m1b1d8, m2b1d8));

        Assertions.assertFalse(res.stream().anyMatch(e -> e.getMessage().equalsIgnoreCase("ATRC Method must be [0-5], 7 or 10")));
    }
}
