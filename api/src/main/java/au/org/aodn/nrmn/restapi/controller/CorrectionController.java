package au.org.aodn.nrmn.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.org.aodn.nrmn.restapi.repository.CorrectionRowRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/correction")
@Tag(name = "ingestion")
public class CorrectionController {

    @Autowired
    private CorrectionRowRepository correctionRowRepository;

    @GetMapping(path = "correct/{survey_id}")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<?> getSurveyCorrection(@PathVariable("survey_id") Long surveyId) {
        var rows = correctionRowRepository.findRowsBySurveyId(surveyId);
        return rows != null && rows.size() > 0 ? ResponseEntity.ok(rows) : ResponseEntity.notFound().build();
    }
}
