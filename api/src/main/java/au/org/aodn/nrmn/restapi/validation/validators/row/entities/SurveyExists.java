package au.org.aodn.nrmn.restapi.validation.validators.row.entities;

import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.validation.validators.base.BaseFormattedValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import cyclops.control.Validated;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationCategory.DATA;
import static au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel.BLOCKING;

public class SurveyExists extends BaseFormattedValidator {

    private static final Integer[] METHODS_TO_CHECK = {0, 1, 2, 7, 10};
    private final SurveyRepository surveyRepository;

    public SurveyExists(SurveyRepository surveyRepository) {
        super("survey");
        this.surveyRepository = surveyRepository;
    }

    @Override
    public Validated<StagedRowError, String> valid(StagedRowFormatted target) {
        if (!Arrays.asList(METHODS_TO_CHECK).contains(target.getMethod())) {
            return Validated.valid("Doesn't apply to method");
        }
        List<Survey> existingSurveys = surveyRepository.findBySiteDepthSurveyNumDate(
                target.getSite(), target.getDepth(), target.getSurveyNum(),
                toDate(target));
                
        if (existingSurveys.isEmpty()) {
            return Validated.valid("New survey");
        }
        
        Survey existingSurvey = existingSurveys.stream().findFirst().get();

        return invalid(target, "Survey " + existingSurvey.getSurveyId() + " includes [" +
                        target.getSite().getSiteCode() + ", " + target.getDate() + ", " + target.getDepth() + "]",
                DATA, BLOCKING, Optional.empty());
    }

    private Date toDate(StagedRowFormatted target) {
        return Date.from(target.getDate().atStartOfDay().atZone(ZoneId.systemDefault())
                               .toInstant());
    }
}
