package au.org.aodn.nrmn.restapi.service;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import au.org.aodn.nrmn.restapi.dto.species.SpeciesRecordDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WormsServiceIT {

    protected MockWebServer mockWebServer;

    protected ObjectMapper mapper = new ObjectMapper();

    protected void createBasisWebClientWithDefaultResult(int size) {
        List<SpeciesRecordDto> records = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            SpeciesRecordDto speciesRecord= new SpeciesRecordDto();
            speciesRecord.aphiaId = i;

            records.add(speciesRecord);
        }

        createBasisWebClientWithResult(records);
    }

    protected void createBasisWebClientWithResult(final List<SpeciesRecordDto> target) {

        Dispatcher dispatcher = new Dispatcher() {

            @Override
            public @NotNull MockResponse dispatch (RecordedRequest request) {

                switch (Objects.requireNonNull(request.getPath()).substring(0, request.getPath().indexOf("?"))) {
                    case "/api/v1/species/AphiaRecordsByName/anyworks": {
                        try {
                            // We need to extract the offset
                            int offset = Integer.parseInt(Objects.requireNonNull(request.getRequestUrl().queryParameter("offset"))) - 1;
                            List<SpeciesRecordDto> item = target.subList(offset, target.size());
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

        List<SpeciesRecordDto> results = wormsService.partialSearch(0, 50, "anyworks");
        Assertions.assertEquals(14, results.size(), "Record size match");

        results = wormsService.partialSearch(0, 10, "anyworks");
        Assertions.assertEquals(10, results.size(), "Record size match");

        results = wormsService.partialSearch(1, 10, "anyworks");
        Assertions.assertEquals(4, results.size(), "Record size match");
    }

    @Test
    public void partialSearchReturnResultEqualsThanFifty() {

        createBasisWebClientWithDefaultResult(50);

        WebClient mock = WebClient.create(mockWebServer.url("/api/v1/species").toString());
        WormsService wormsService = new WormsService(mock);

        List<SpeciesRecordDto> results = wormsService.partialSearch(0, 50, "anyworks");
        Assertions.assertEquals(50, results.size(), "Record size match");

        results = wormsService.partialSearch(0, 10, "anyworks");
        Assertions.assertEquals(10, results.size(), "Record size match");

        results = wormsService.partialSearch(1, 50, "anyworks");
        Assertions.assertEquals(0, results.size(), "Record size match");
    }

    @Test
    public void partialSearchReturnResultMoreThanFifty() {

        createBasisWebClientWithDefaultResult(120);

        WebClient mock = WebClient.create(mockWebServer.url("/api/v1/species").toString());
        WormsService wormsService = new WormsService(mock);

        List<SpeciesRecordDto> results = wormsService.partialSearch(0, 50, "anyworks");
        Assertions.assertEquals(50, results.size(), "Record size match");

        results = wormsService.partialSearch(1, 50, "anyworks");
        Assertions.assertEquals(50, results.size(), "Record size match");

        results = wormsService.partialSearch(2, 50, "anyworks");
        Assertions.assertEquals(20, results.size(), "Record size match");
    }
}
