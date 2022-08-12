package au.org.aodn.nrmn.restapi.validation.process;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.dto.stage.ValidationError;
import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;

@ExtendWith(MockitoExtension.class)
class ATRCDepthValidationTest {

  @Mock
  ObservationRepository observationRepository;

  @Mock
  DiverRepository diverRepository;

  @Mock
  SiteRepository siteRepository;

  @InjectMocks
  ValidationProcess validationProcess;

  @Test
  void nullDepthShouldFail() {
    StagedJob job = new StagedJob();
    job.setId(1L);
    StagedRow row = new StagedRow();
    row.setDate("");
    row.setDepth("");
    row.setBlock("");
    row.setStagedJob(job);
    Collection<ValidationError> errors = validationProcess.checkFormatting(ProgramValidation.ATRC, false, Arrays.asList(),
        Arrays.asList(), Arrays.asList(row));
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals("Depth is invalid, expected: depth[.surveyNum]")));
  }

  @Test
  void depthWithTransectOutOfRangeShouldFail() {
    StagedJob job = new StagedJob();
    job.setId(1L);
    StagedRowFormatted stage = new StagedRowFormatted();
    stage.setSurveyNum(9);
    ValidationError error = validationProcess.validateSurveyTransectNumber(Arrays.asList(stage));
    assertTrue(error.getMessage().equals("Survey group transect invalid"));
  }

  @Test
  void depthWithTransectInRangeForMethod1ShouldSucceed() {
    StagedJob job = new StagedJob();
    job.setId(1L);
    StagedRowFormatted row = new StagedRowFormatted();
    row.setDepth(7);
    row.setSurveyNum(3);
    row.setMethod(1);
    ValidationError error = validationProcess.validateSurveyTransectNumber(Arrays.asList(row));
    assertTrue(error == null);
  }
}
