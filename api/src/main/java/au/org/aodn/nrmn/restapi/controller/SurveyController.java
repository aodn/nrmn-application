package au.org.aodn.nrmn.restapi.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.FilterCondition;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.ObservationFilterCondition;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.SurveyFilterCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
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

    protected Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private SurveyEditService surveyEditService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private ObjectMapper objMapper;

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

        // Diver name search need another table, we do not need to apply sorting here as it is just intermediate result.
        Optional<Filter> diverFilter = ObservationFilterCondition.getSupportField(f, ObservationFilterCondition.SupportedFilters.DIVER_NAME_IN_SURVEY);
        if(diverFilter.isPresent()) {
            // Find a list of observation where diver name matches diver filters
            Specification<Observation> observationSpecification = ObservationFilterCondition.createSpecification(f);

            if(observationSpecification != null) {
                // Now we can use the observation to link to the survey id that matches diver filters
                List<Observation> o = observationRepository.findAll(observationSpecification);

                // To avoid table join in object level, extract ids and use it in another query.
                List<Integer> observationIds = o.stream()
                        .map(observation -> observation.getObservationId())
                        .collect(Collectors.toList());

                // We need to partition it because of the length limitation of a parameter in query
                List<List<Integer>> partitionObvIds = ListUtils.partition(observationIds, 5000);

                // With set ids will be unique
                Set<Integer> temp = new HashSet<>();

                partitionObvIds
                        .stream()
                        .forEach(p -> temp.addAll(surveyRepository.getSurveyIdForObservation(p)));

                List<String> ids = temp.stream().map(String::valueOf).collect(Collectors.toList());

                if(!ids.isEmpty()) {
                    // Add filter, so that we only bound the result given these survey ids
                    f.add(new Filter(
                            SurveyFilterCondition.SupportedFields.SURVEY_ID.toString(),
                            String.join(",", ids),
                            SurveyFilterCondition.IN,
                            null));
                }
                else {
                    // User search by diver, but non of the observation id matches, we put a condition that cause
                    // the search return nothing.
                    f.add(new Filter(
                            SurveyFilterCondition.SupportedFields.SURVEY_ID.toString(),
                            ",",
                            SurveyFilterCondition.IN,
                            null));
                }
            }
        }

        var surveyRows = surveyRepository.findAllProjectedBy(f, s, PageRequest.of(page, pageSize));

        var surveyIds = surveyRows.stream()
                .map(sid -> sid.getSurveyId())
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, String> diverNames = surveyRepository.getDiversForSurvey(surveyIds).stream()
                .collect(Collectors.groupingBy(SurveyRowDivers::getSurveyId,
                        Collectors.mapping(SurveyRowDivers::getDiverName, Collectors.joining(", "))));

        var surveyRowsWithDivers = surveyRows.stream().map(z -> {
            z.setDiverNames(diverNames.get(z.getSurveyId()));
            return z;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();

        data.put("lastRow", surveyRows.getTotalElements());
        data.put("items", surveyRowsWithDivers);

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
