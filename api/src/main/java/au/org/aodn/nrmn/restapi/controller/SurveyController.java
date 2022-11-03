package au.org.aodn.nrmn.restapi.controller;

import java.util.*;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.data.model.Program;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.repository.ProgramRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyListRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.data.repository.dynamicQuery.FilterCondition;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.service.MaterializedViewService;
import au.org.aodn.nrmn.restapi.service.SurveyEditService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Surveys")
@RequestMapping(path = "/api/v1/data")
public class SurveyController {

    protected Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private SurveyEditService surveyEditService;

    @Autowired
    private MaterializedViewService materializedViewService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private SurveyListRepository surveyListRepository;
    /**
     *
     * @param page
     * @param pageSize - AgGrid use 100 as default page size
     * @return
     */
    @GetMapping(path = "/surveys")
    public ResponseEntity<?> listMatching(@RequestParam(value = "sort", required = false) String sort,
                                          @RequestParam(value = "filters", required = false) String filters,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) throws JsonProcessingException {

        // RequestParam do not support json object parsing automatically
        List<Filter> f = FilterCondition.parse(objMapper, filters, Filter[].class);
        List<Sorter> s = FilterCondition.parse(objMapper, sort, Sorter[].class);

        var surveyRows = surveyListRepository.findAllProjectedBy(f, s, PageRequest.of(page, pageSize));

        Map<String, Object> data = new HashMap<>();

        data.put("lastRow", surveyRows.getTotalElements());
        data.put("items", surveyRows.getContent());

        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/programs")
    public ResponseEntity<List<Program>> getSurveyPrograms() {
        return ResponseEntity.ok(programRepository.findActive());
    }

    @GetMapping("/survey/{id}")
    public ResponseEntity<SurveyDto> findOne(@PathVariable Integer id) {
        return surveyRepository.findById(id).map(survey -> ResponseEntity.ok(mapper.map(survey, SurveyDto.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/survey/{id}")
    public ResponseEntity<?> findOne(@Valid @RequestBody SurveyDto surveyDto) {

        var errors = surveyEditService.validateSurvey(surveyDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Survey survey = surveyEditService.updateSurvey(surveyDto);

        Survey persistedSurvey = surveyRepository.save(survey);
        materializedViewService.refreshAllMaterializedViews();
        SurveyDto updatedSurveyDto = mapper.map(persistedSurvey, SurveyDto.class);
        return ResponseEntity.ok(updatedSurveyDto);

    }
}
