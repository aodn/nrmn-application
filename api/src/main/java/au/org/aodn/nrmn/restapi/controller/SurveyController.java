package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.repository.projections.SurveyRow;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@Tag(name = "surveys")
@RequestMapping(path = "/api/data")
public class SurveyController {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ModelMapper mapper;

    @GetMapping(path = "/surveys")
    public ResponseEntity<List<SurveyRow>> list() {
        return ResponseEntity.ok(surveyRepository.findAllProjectedBy().stream().collect(Collectors.toList()));
    }

    @GetMapping("/survey/{id}")
    public ResponseEntity<SurveyDto> findOne(@PathVariable Integer id) {
        return surveyRepository.findById(id).map(survey -> 
            ResponseEntity.ok(mapper.map(survey, SurveyDto.class)))
        .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/survey/{id}")
    public ResponseEntity<SurveyDto> findOne(@Valid @RequestBody SurveyDto surveyDto) {

        Optional<Survey> survey = surveyRepository.findById(surveyDto.getSurveyId());
        
        if(!survey.isPresent()) 
            return ResponseEntity.notFound().build();

        Survey updatedSurvey = survey.get();
        updatedSurvey.setProtectionStatus(surveyDto.getProtectionStatus());
        updatedSurvey.setNotes(surveyDto.getNotes());
        updatedSurvey.setProjectTitle(surveyDto.getProjectTitle());
        updatedSurvey.setInsideMarinePark(surveyDto.getInsideMarinePark());

        return ResponseEntity.ok(mapper.map(surveyRepository.save(updatedSurvey), SurveyDto.class));
    }
}
