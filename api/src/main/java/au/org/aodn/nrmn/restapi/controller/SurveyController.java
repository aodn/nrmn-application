package au.org.aodn.nrmn.restapi.controller;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;
import au.org.aodn.nrmn.restapi.model.db.Observation;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.ObservationFilterCondition;
import au.org.aodn.nrmn.restapi.repository.dynamicQuery.SurveyFilterCondition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.ListUtils;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<?> listMatching(@RequestParam(value = "filters", required = false) String filters,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) throws JsonProcessingException {

        // RequestParam do not support json object parsing automatically
        List<Filter> f = filters != null ? Arrays.stream((objMapper.readValue(filters, Filter[].class))).collect(Collectors.toList()) : null;

        // Diver name search need another table
        Optional<Filter> diverFilter = ObservationFilterCondition.getFilter(f, ObservationFilterCondition.SupportedFilters.DIVER_NAME);
        if(diverFilter.isPresent()) {

            if(diverFilter.get().isCompositeCondition()) {
                // Diver name need special handle for composite selection, as observation to diver is 1 to 1 map,
                // if use need AND operation between two filters for diver name, what it really means in db level is
                // select observation's survey id, where either comes from diver 1 OR diver 2 therefore count
                // survey id will > 1
                diverFilter.get().setOperation(Filter.OR);
            }

            Specification<Observation> observationSpecification = ObservationFilterCondition.createSpecification(f);

            if(observationSpecification != null) {
                // User wants to filter by diver name, we need to get the list of survey id that matches and
                // add it to the filter for next search
                List<Observation> o = observationRepository.findAll(observationSpecification);
                List<Integer> obs_ids = o.stream()
                        .map(m -> m.getObservationId())
                        .distinct()
                        .collect(Collectors.toList());

                if(!obs_ids.isEmpty()) {
                    // We need a special query to get survey id given observation id, id can
                    // be very long and exceed sql param limit and hence we need to split it in group and query them
                    List<List<Integer>> partitionIds = ListUtils.partition(obs_ids, 15000);

                    // Store all survey id first
                    List<SurveyRowDivers> ids = Collections.synchronizedList(new ArrayList<>());

                    partitionIds.parallelStream().forEach(l -> {
                        ids.addAll(surveyRepository.getSurveyFromObservation(l));
                    });

                    Set<Integer> uniqueIds;

                    if(diverFilter.get().isCompositeCondition()) {
                        // Find all id where count > 1, we cannot do it at sql level due to parallelStream()
                        List<Integer> d = ids.stream()
                                .collect(Collectors.groupingBy(i -> i.getSurveyId(), Collectors.counting()))
                                .entrySet()
                                .stream()
                                .filter(e -> e.getValue() > 1)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

                        uniqueIds = new HashSet<>(d);
                    }
                    else {
                        // Make is unique by add to set
                        uniqueIds = new HashSet<>(ids.stream().map(m -> m.getSurveyId()).collect(Collectors.toList()));
                    }

                    // Expend and add filter, you should never see empty ids here
                    f.add(new Filter(
                            SurveyFilterCondition.SupportedFilters.SURVEY_ID.toString(),
                            String.join(",", uniqueIds.stream().map(i -> i.toString()).collect(Collectors.toList())),
                            SurveyFilterCondition.IN,
                            null,
                            null));
                }
                else {
                    // User search by diver, but non of the observation id matches, we put a condition that cause
                    // the search return nothing.
                    f.add(new Filter(
                            SurveyFilterCondition.SupportedFilters.SURVEY_ID.toString(),
                            ",",
                            SurveyFilterCondition.IN,
                            null,
                            null));
                }
            }
        }

        var surveyRows = surveyRepository
                .findAllProjectedBy(f, PageRequest.of(page, pageSize));

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
