package au.org.aodn.nrmn.restapi.validation.data;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import au.org.aodn.nrmn.restapi.data.model.StagedJob;
import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.enums.ProgramValidation;
import au.org.aodn.nrmn.restapi.service.validation.DataValidation;
import au.org.aodn.nrmn.restapi.service.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.service.validation.SurveyValidation;

@ExtendWith(MockitoExtension.class)
class ATRCDepthValidationTest {

  @Mock
  ObservationRepository observationRepository;

  @Mock
  DiverRepository diverRepository;

  @Mock
  SiteRepository siteRepository;

  @InjectMocks
  DataValidation dataValidation;

  @InjectMocks
  SurveyValidation surveyValidation;

  @Test
  void nullDepthShouldFail() {
    StagedJob job = new StagedJob();
    job.setId(1L);
    StagedRow row = new StagedRow();
    row.setDate("");
    row.setDepth("");
    row.setBlock("");
    row.setStagedJob(job);
    Collection<SurveyValidationError> errors = dataValidation.checkFormatting(ProgramValidation.ATRC, false, true, Arrays.asList(),
        Arrays.asList(), Arrays.asList(row));
    assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals("Depth is invalid, expected: depth[.surveyNum]")));
  }

  @Test
  void depthWithTransectOutOfRangeShouldFail() {
    StagedJob job = new StagedJob();
    job.setId(1L);
    StagedRowFormatted stage = new StagedRowFormatted();
    stage.setSurveyNum(9);
    SurveyValidationError error = surveyValidation.validateSurveyTransectNumber(Arrays.asList(stage));
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
    SurveyValidationError error = surveyValidation.validateSurveyTransectNumber(Arrays.asList(row));
    assertTrue(error == null);
  }
}
