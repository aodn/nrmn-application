package au.org.aodn.nrmn.restapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.google.common.primitives.Ints;

import au.org.aodn.nrmn.restapi.RestApiApplication;
import au.org.aodn.nrmn.restapi.controller.utils.RequestWrapper;
import au.org.aodn.nrmn.restapi.data.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.data.repository.StagedJobRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyRepository;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesCorrectBodyDto;
import au.org.aodn.nrmn.restapi.security.JwtTokenProvider;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Testcontainers
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
class CorrectionsControllerIT {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    StagedJobRepository stagedJobRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    ObservationRepository observationRepository;

    @LocalServerPort
    int localServerPort;

    @Test
    @WithUserDetails("test@example.com")
    public void speciesCorrects() throws Exception {

        var reqBuilder = new RequestWrapper<SpeciesCorrectBodyDto, Long>();
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);
        var uri = String.format("http://localhost:%d/api/v1/correction/correctSpecies", localServerPort);
        var prevCount = observationRepository.findAll().size();

        var speciesCorrection = new SpeciesCorrectBodyDto();
        speciesCorrection.setPrevObservableItemId(333);
        speciesCorrection.setNewObservableItemId(331);
        speciesCorrection.setSurveyIds(Ints.asList(812300133));

        var response = reqBuilder
                .withUri(uri)
                .withMethod(HttpMethod.POST)
                .withToken(token)
                .withEntity(speciesCorrection)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var observations = observationRepository.findAll();
        var nextCount = observations.size();
        var prevObservableItemCount = observations.stream().filter(o -> o.getObservableItem().getObservableItemId() == 333).count();
        var nextObservableItemCount = observations.stream().filter(o -> o.getObservableItem().getObservableItemId() == 331).count();

        assertEquals(prevCount, nextCount);
        assertEquals(0, prevObservableItemCount);
        assertEquals(nextCount, nextObservableItemCount);
    }
    /**
     * If you make a request to get these survey ids and one of it is a locked survey. Then the whole request will fail.
     * The UI may not be able to create such request but direct call to API can.
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void requestSurveyWithLockedFailedAll() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // We will make a request where the return code is a bad request, so using Void return type is fine.
        var reqBuilder = new RequestWrapper<Void, Map>();

        // First we need to figure out which survey id is locked which not
        var surveys = reqBuilder
                .withUri(String.format("http://localhost:%d/api/v1/data/surveys", localServerPort))
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withAppJson()
                .withContentType(MediaType.APPLICATION_JSON)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, surveys.getStatusCode());

        // We want a survey locked and one unlock
        assertTrue("Return object greater than 1", (Integer)surveys.getBody().get("lastRow") > 0);

        // Now find the survey id where the item is locked and not locked
        var locked = ((List<Map<String, Object>>)surveys.getBody().get("items"))
                .stream()
                .filter(i -> (Boolean)i.get("locked") == Boolean.TRUE)
                .map(i -> i.get("surveyId").toString())
                .collect(Collectors.toList());

        var unlocked = ((List<Map<String, Object>>)surveys.getBody().get("items"))
                .stream()
                .filter(i -> (Boolean)i.get("locked") == Boolean.FALSE)
                .map(i -> i.get("surveyId").toString())
                .collect(Collectors.toList());


        // Survey with locked only id
        var param = new HashMap<String, String>() {{
            put("surveyIds", String.join(",", locked));
        }};

        var uri = String.format("http://localhost:%d/api/v1/correction/correct?surveyIds={surveyIds}", localServerPort);
        var reqBuilder1 = new RequestWrapper<Void, Void>();
        var response = reqBuilder1
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Void.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Now even a locked survey together with non-locked survey result in same bad request error
        param.put("surveyIds", String.join(",", locked) + "," + String.join(",", unlocked));
        response = reqBuilder1
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Void.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // If just non-locked survey then it is ok
        param.put("surveyIds", String.join(",", unlocked));
        response = reqBuilder1
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Void.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
