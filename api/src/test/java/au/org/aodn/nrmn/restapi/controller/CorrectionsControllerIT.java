package au.org.aodn.nrmn.restapi.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import au.org.aodn.nrmn.restapi.data.model.StagedRow;
import au.org.aodn.nrmn.restapi.dto.correction.CorrectionRequestBodyDto;
import au.org.aodn.nrmn.restapi.dto.correction.SpeciesSearchBodyDto;
import au.org.aodn.nrmn.restapi.dto.stage.SurveyValidationError;
import au.org.aodn.nrmn.restapi.dto.stage.ValidationResponse;
import au.org.aodn.nrmn.restapi.enums.ValidationCategory;
import au.org.aodn.nrmn.restapi.enums.ValidationLevel;
import org.apache.commons.io.FileUtils;
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
import org.springframework.util.ResourceUtils;
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

import java.nio.charset.Charset;
import java.util.*;
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
                .withResponseType(Long.class)
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
        var body =  surveys.getBody();
        assertNotNull(body);
        assertTrue("Return object greater than 1", (Integer)body.get("lastRow") > 0);

        // Now find the survey id where the item is locked and not locked
        var locked = ((List<Map<String, Object>>) body.get("items"))
                .stream()
                .filter(i -> i.get("locked") == Boolean.TRUE)
                .map(i -> i.get("surveyId").toString())
                .collect(Collectors.toList());

        // pq_catalogued and locked both affect the request operation, so filter out pq_catalogued too
        var unlocked = ((List<Map<String, Object>>) body.get("items"))
                .stream()
                .filter(i -> i.get("locked") == Boolean.FALSE)
                .map(i -> i.get("surveyId").toString())
                .collect(Collectors.toList());


        // Survey with locked only id
        var param = new HashMap<String, String>() {{
            put("surveyIds", String.join(",", locked));
        }};

        var uri = String.format("http://localhost:%d/api/v1/correction/correct?surveyIds={surveyIds}", localServerPort);
        var reqBuilder1 = new RequestWrapper<Void, String>();
        var response = reqBuilder1
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(String.class)
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
                .withResponseType(String.class)
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
                .withResponseType(String.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * You must request correction survey share the same program validation, otherwise
     * you will receive error
     *
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void requestSurveyWithDiffProgramValidationFailedAll() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Survey with ATRC and RLS which belongs to different program
        var param = new HashMap<String, String>() {{
            put("surveyIds", "812300132,812331346");
        }};

        var uri = String.format("http://localhost:%d/api/v1/correction/correct?surveyIds={surveyIds}", localServerPort);
        // We will make a request where the return code is a bad request, so using Void return type is fine.
        var reqBuilder = new RequestWrapper<Void, Map>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Same program id will have no issue
        param.put("surveyIds", "812300131,812300133");
        response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withMethod(HttpMethod.GET)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithUserDetails("test@example.com")
    public void validateSurveyCorrectionEmptyDto() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Survey with ATRC and RLS which belongs to different program
        var param = new HashMap<String, String>() {{
            put("surveyIds", "812300132,812331346");
        }};

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        d.setProgramId(55);
        d.setRows(new ArrayList<>());

        var uri = String.format("http://localhost:%d/api/v1/correction/validate?surveyIds={surveyIds}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, ValidationResponse>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(ValidationResponse.class)
                .build(testRestTemplate);

        // Empty body should result in BLOCKING error mentioned in Body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("2 error returned", 2, body.getErrors().size());

        List<SurveyValidationError> e = (List<SurveyValidationError>) body.getErrors();
        
        assertEquals("Expect blocking level error", e.get(0).getLevelId(), ValidationLevel.BLOCKING);
        assertEquals("Data level error", e.get(0).getCategoryId(), ValidationCategory.DATA);
        assertTrue("Message in error", e.get(0).getMessage().equals("Survey data is missing"));
        
        assertEquals("Expect blocking level error", e.get(1).getLevelId(), ValidationLevel.BLOCKING);
        assertTrue("Survey IDs missing", e.get(1).getMessage().equals("Survey IDs missing: 812300132, 812331346"));
    }
    /**
     * Verify return error on incorrect site code in body
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void validateSurveyCorrectionFieldError() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Survey with ATRC and RLS which belongs to different program
        var param = new HashMap<String, String>() {{
            put("surveyIds", "812300132,812331346");
        }};

        // SiteCode "-1" should cause problem and cause site code not exist erroer
        StagedRow stagedRow = new StagedRow();
        stagedRow.setSiteCode("-1");
        stagedRow.setSurveyId("812300132");
        stagedRow.setDate("01/01/2021");
        stagedRow.setTime("00:00:00");
        stagedRow.setDepth("1");
        stagedRow.setMeasureJson(new HashMap<>());
        stagedRow.setIsInvertSizing("");
        stagedRow.setMethod("2");

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        d.setProgramId(55);
        d.setRows(new ArrayList<>());
        d.getRows().add(stagedRow);

        var uri = String.format("http://localhost:%d/api/v1/correction/validate?surveyIds={surveyIds}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, ValidationResponse>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(ValidationResponse.class)
                .build(testRestTemplate);

        // Empty body should result in BLOCKING error mentioned in Body
        assertEquals(HttpStatus.OK, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);

        assertEquals("9 error returned", body.getErrors().size(), 9);

        List<SurveyValidationError> e = (List<SurveyValidationError>) body.getErrors();

        assertTrue("Block must be 0, 1 or 2", e.get(0).getMessage().equals("Block must be 0, 1 or 2"));
        assertTrue("Site Code does not exist", e.get(1).getMessage().equals("Site Code does not exist"));
        assertTrue("Latitude is not number", e.get(2).getMessage().equals("Latitude is not number"));
        assertTrue("Longitude is not number", e.get(3).getMessage().equals("Longitude is not number"));
        assertTrue("Inverts is not an integer", e.get(4).getMessage().equals("Inverts is not an integer"));
        assertTrue("P-Qs Diver is blank", e.get(5).getMessage().equals("P-Qs Diver is blank"));
        assertTrue("Diver does not exist", e.get(6).getMessage().equals("Diver does not exist"));
        assertTrue("Survey IDs missing: 812331346", e.get(7).getMessage().equals("Survey IDs missing: 812331346"));
        assertTrue("Row has no data and no value recorded for inverts", e.get(8).getMessage().equals("Row has no data and no value recorded for inverts"));
    }
    @Test
    @WithUserDetails("test@example.com")
    public void submitSurveyCorrectionUnknownSurveyId() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Not exist survey Id
        var param = new HashMap<String, String>() {{
            put("surveyIds", "1");
        }};

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        var uri = String.format("http://localhost:%d/api/v1/correction/correct?surveyIds={surveyIds}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, String>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(String.class)
                .build(testRestTemplate);

        // No proper response at the moment, become 500 and screen will blank
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @WithUserDetails("test@example.com")
    public void deleteSurveyUnknownSurveyId() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Not exist survey Id
        var param = new HashMap<String, String>() {{
            put("surveyId", "1");
        }};

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        var uri = String.format("http://localhost:%d/api/v1/correction/correct/{surveyId}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, String>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.DELETE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(String.class)
                .build(testRestTemplate);

        // No proper response at the moment, become 500 and screen will blank
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    /**
     * You cannot delete locked survey
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void deleteLockedSurveyIdFail() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Not exist survey Id
        var param = new HashMap<String, String>() {{
            put("surveyId", "812331346");
        }};

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        var uri = String.format("http://localhost:%d/api/v1/correction/correct/{surveyId}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, Map>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.DELETE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        // No proper response at the moment, become 500 and screen will blank
        assertEquals("Correct status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertTrue("Correct alert message", Objects.nonNull(body) && body.get("message").equals("Deletion Failed. Survey is locked."));
    }
    /**
     * You cannot delete survey with PqCatalogued
     * @throws Exception
     */
    @Test
    @WithUserDetails("test@example.com")
    public void deletePqCataloguedSurveyIdFail() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        // Not exist survey Id
        var param = new HashMap<String, String>() {{
            put("surveyId", "812331347");
        }};

        CorrectionRequestBodyDto d = new CorrectionRequestBodyDto();
        var uri = String.format("http://localhost:%d/api/v1/correction/correct/{surveyId}", localServerPort);
        var reqBuilder = new RequestWrapper<CorrectionRequestBodyDto, Map>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(d)
                .withMethod(HttpMethod.DELETE)
                .withContentType(MediaType.APPLICATION_JSON)
                .withParams(param)
                .withResponseType(Map.class)
                .build(testRestTemplate);

        // No proper response at the moment, become 500 and screen will blank
        assertEquals("Correct status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertTrue("Correct alert message", Objects.nonNull(body) && body.get("message").equals("Deletion Failed. PQs catalogued for this survey."));
    }
    @Test
    @WithUserDetails("test@example.com")
    public void testSpeciesSearch() throws Exception {
        var auth = getContext().getAuthentication();
        var token = jwtTokenProvider.generateToken(auth);

        SpeciesSearchBodyDto ss = new SpeciesSearchBodyDto();
        ss.setStartDate("01/01/2006");
        ss.setEndDate("12/12/2006");
        ss.setObservableItemId(333);

        // This kml will cover the whole area of Australia
        ss.setGeometry(
                FileUtils.readFileToString(
                        ResourceUtils.getFile("classpath:testdata/testlayer.kml"),
                        Charset.defaultCharset()
                ));

        var uri = String.format("http://localhost:%d/api/v1/correction/searchSpecies", localServerPort);
        var reqBuilder = new RequestWrapper<SpeciesSearchBodyDto, String>();
        var response = reqBuilder
                .withUri(uri)
                .withToken(token)
                .withEntity(ss)
                .withMethod(HttpMethod.POST)
                .withContentType(MediaType.APPLICATION_JSON)
                .withResponseType(String.class)
                .build(testRestTemplate);

        // No proper response at the moment, become 500 and screen will blank
        assertEquals("Correct status code", HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}