package au.org.aodn.nrmn.restapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyFilterDto;
import au.org.aodn.nrmn.restapi.model.db.Program;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.repository.projections.SurveyRowDivers;
import au.org.aodn.nrmn.restapi.service.SurveyEditService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Surveys")
@RequestMapping(path = "/api/v1/data")
public class SurveyController {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private SurveyEditService surveyEditService;

    @Autowired
    private ModelMapper mapper;

    /**
     *
     * @param surveyFilter
     * @param page
     * @param pageSize - AgGrid use 100 as default page size
     * @return
     */
    @GetMapping(path = "/surveys")
    public ResponseEntity<?> listMatching(SurveyFilterDto surveyFilter,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {

        if (surveyFilter.isSet()) {
            return ResponseEntity
                    .ok(surveyRepository.findByCriteria(surveyFilter).stream().collect(Collectors.toList()));
        } else {

            var surveyRows = surveyRepository.findAllProjectedBy(PageRequest.of(page, pageSize));
            var surveyIds = surveyRows.stream()
                    .map(s -> s.getSurveyId())
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, String> diverNames = surveyRepository.getDiversForSurvey(surveyIds).stream()
                    .collect(Collectors.groupingBy(SurveyRowDivers::getSurveyId,
                            Collectors.mapping(SurveyRowDivers::getDiverName, Collectors.joining(", "))));

            var surveyRowsWithDivers = surveyRows.stream().map(s -> {
                s.setDiverNames(diverNames.get(s.getSurveyId()));
                return s;
            }).collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();

            data.put("lastRow", surveyRows.getTotalElements());
            data.put("items", surveyRowsWithDivers);

            return ResponseEntity.ok(data);
        }
    }

    @GetMapping(path = "/programs")
    public ResponseEntity<List<Program>> getSurveyPrograms() {
        return ResponseEntity.ok(programRepository.findAll());
    }

    @GetMapping("/survey/{id}")
    public ResponseEntity<SurveyDto> findOne(@PathVariable Integer id) {
        return surveyRepository.findById(id).map(survey -> ResponseEntity.ok(mapper.map(survey, SurveyDto.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/survey/{id}")
    public ResponseEntity<?> findOne(@Valid @RequestBody SurveyDto surveyDto) {

        ValidationErrors errors = surveyEditService.validateSurvey(surveyDto);
        if (!errors.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Survey survey = surveyEditService.updateSurvey(surveyDto);

        Survey persistedSurvey = surveyRepository.save(survey);
        SurveyDto updatedSurveyDto = mapper.map(persistedSurvey, SurveyDto.class);
        return ResponseEntity.ok(updatedSurveyDto);

    }
}
