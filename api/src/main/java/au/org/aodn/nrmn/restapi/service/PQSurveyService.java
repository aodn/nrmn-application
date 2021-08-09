package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PQSurveyService {

    private final WebClient rlsClient;

    private static Logger logger = LoggerFactory.getLogger(PQSurveyService.class);

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    public PQSurveyService(@Qualifier("rlsClient") WebClient rlsClient) {
        this.rlsClient = rlsClient;
    }

    public void updatePQSurveyFlags() {

        logger.info("Starting update of survey PQ zip files");

        String responseJson = rlsClient.get().uri(uriBuilder -> uriBuilder.path("/pq/survey_ids/").build())
                .exchangeToMono(response -> response.bodyToMono(String.class)).block();

        JsonNode jsonNode = null;
        try {
            jsonNode = (new ObjectMapper()).readTree(responseJson);
        } catch (JsonProcessingException e) {
            logger.error("Could not parse json response");
            e.printStackTrace();
            return;
        }

        HashMap<Integer, String> surveyZipUrls = new HashMap<>();

        if (jsonNode != null) {
            jsonNode.get("results").forEach(survey -> {
                Integer surveyId = survey.get("survey_id").asInt();
                String zipUrl = survey.get("urls").get(2).get("zip").asText();
                surveyZipUrls.put(surveyId, zipUrl);
            });
        }

        List<Survey> updatedSurveys = surveyRepository
                .findSurveysWithoutPQ(new ArrayList<Integer>(surveyZipUrls.keySet())).stream().map(survey -> {
                    survey.setPqCatalogued(true);
                    survey.setPqZipUrl(surveyZipUrls.get(survey.getSurveyId()));
                    return survey;
                }).collect(Collectors.toList());

        surveyRepository.saveAll(updatedSurveys);

        logger.info(String.format("Updates PQ zip file urls for %d surveys", updatedSurveys.size()));

    }
}
