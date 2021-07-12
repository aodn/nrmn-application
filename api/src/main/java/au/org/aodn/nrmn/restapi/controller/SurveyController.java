package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyFilterDto;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRow;
import au.org.aodn.nrmn.restapi.service.SurveyEditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "surveys")
@RequestMapping(path = "/api/data")
public class SurveyController {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private DiverRepository diverRepository;

    @Autowired
    private SurveyEditService surveyEditService;

    @Autowired
    private ModelMapper mapper;

    @GetMapping(path = "/surveys")
    public ResponseEntity<List<SurveyRow>> listMatching(SurveyFilterDto surveyFilter) {
        if(surveyFilter.isSet())
            return ResponseEntity.ok(surveyRepository.findByCriteria(surveyFilter).stream().collect(Collectors.toList()));
        else
            return ResponseEntity.ok(surveyRepository.findAllProjectedBy().stream().collect(Collectors.toList()));
    }
    
    @GetMapping("/survey/{id}")
    public ResponseEntity<SurveyDto> findOne(@PathVariable Integer id) {
        return surveyRepository.findById(id).map(survey -> 
            ResponseEntity.ok(mapper.map(survey, SurveyDto.class)))
        .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/survey/{id}")
    public ResponseEntity<?> findOne(@Valid @RequestBody SurveyDto surveyDto) {

        ValidationErrors errors = surveyEditService.validateSurvey(surveyDto);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Survey survey = surveyRepository.findById(surveyDto.getSurveyId()).orElseThrow(ResourceNotFoundException::new);
        surveyEditService.updateSurvey(surveyDto);

        Survey persistedSurvey = surveyRepository.save(survey);
        SurveyDto updatedSurveyDto = mapper.map(persistedSurvey, SurveyDto.class);
        return ResponseEntity.ok().body(updatedSurveyDto);

    }
}
