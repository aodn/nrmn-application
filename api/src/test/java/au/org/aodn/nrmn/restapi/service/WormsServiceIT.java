package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.service.model.SpeciesRecord;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WormsServiceIT {

    protected MockWebServer mockWebServer;

    protected ObjectMapper mapper = new ObjectMapper();

    protected void createBasisWebClientWithDefaultResult(int size) {
        List<SpeciesRecord> records = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            SpeciesRecord speciesRecord= new SpeciesRecord();
            speciesRecord.aphiaId = i;

            records.add(speciesRecord);
        }

        createBasisWebClientWithResult(records);
    }

    protected void createBasisWebClientWithResult(final List<SpeciesRecord> target) {

        Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch (RecordedRequest request) {

                switch (request.getPath().substring(0, request.getPath().indexOf("?"))) {
                    case "/api/v1/species/AphiaRecordsByName/anyworks": {
                        try {
                            // We need to extract the offset
                            int offset = Integer.parseInt(request.getRequestUrl().queryParameter("offset")) - 1;
                            List<SpeciesRecord> item = target.subList(offset, target.size());
                            item = item.subList(0, (item.size() >= 50 ? 50 : item.size()));

                            return new MockResponse()
                                    .setResponseCode(200)
                                    .setBody(mapper.writeValueAsString(item))
                                    .addHeader("Content-Type", "application/json");
                        }
                        catch (JsonProcessingException e) {
                            return new MockResponse()
                                    .setResponseCode(500);
                        }
                    }

                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };
        mockWebServer.setDispatcher(dispatcher);
    }

    @BeforeEach
    public void init() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        if(mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }
    /**
     * By default the api return result of 50 in one query, however you can get less then 50 for some species
     */
    @Test
    public void partialSearchReturnResultLessThanFifty() {

        createBasisWebClientWithDefaultResult(14);

        WebClient mock = WebClient.create(mockWebServer.url("/api/v1/species").toString());
        WormsService wormsService = new WormsService(mock);

        List<SpeciesRecord> results = wormsService.partialSearch(0, 50, "anyworks");
        assertEquals("Record size match", 14, results.size());

        results = wormsService.partialSearch(0, 10, "anyworks");
        assertEquals("Record size match", 10, results.size());

        results = wormsService.partialSearch(1, 10, "anyworks");
        assertEquals("Record size match", 4, results.size());
    }

    @Test
    public void partialSearchReturnResultEqualsThanFifty() {

        createBasisWebClientWithDefaultResult(50);

        WebClient mock = WebClient.create(mockWebServer.url("/api/v1/species").toString());
        WormsService wormsService = new WormsService(mock);

        List<SpeciesRecord> results = wormsService.partialSearch(0, 50, "anyworks");
        assertEquals("Record size match", 50, results.size());

        results = wormsService.partialSearch(0, 10, "anyworks");
        assertEquals("Record size match", 10, results.size());

        results = wormsService.partialSearch(1, 50, "anyworks");
        assertEquals("Record size match", 0, results.size());
    }

    @Test
    public void partialSearchReturnResultMoreThanFifty() {

        createBasisWebClientWithDefaultResult(120);

        WebClient mock = WebClient.create(mockWebServer.url("/api/v1/species").toString());
        WormsService wormsService = new WormsService(mock);

        List<SpeciesRecord> results = wormsService.partialSearch(0, 50, "anyworks");
        assertEquals("Record size match", 50, results.size());

        results = wormsService.partialSearch(1, 50, "anyworks");
        assertEquals("Record size match", 50, results.size());

        results = wormsService.partialSearch(2, 50, "anyworks");
        assertEquals("Record size match", 20, results.size());
    }
}
