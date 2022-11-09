package au.org.aodn.nrmn.restapi.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
}
