package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WormsService implements AphiaRefService {

    private final WebClient wormsClient;

    @Autowired
    public WormsService(@Qualifier("wormsClient") WebClient wormsClient) {
        this.wormsClient = wormsClient;
    }

    @Override
    public List<AphiaRef> fuzzyNameSearch(String searchTerm) {
        Mono<AphiaRef[][]> response = wormsClient
                .get().uri(uriBuilder ->
                        uriBuilder.path("/AphiaRecordsByMatchNames")
                                  .queryParam("scientificnames[]", searchTerm)
                                  .build())
                .retrieve()
                .bodyToMono(AphiaRef[][].class);
        AphiaRef[][] aphiaRefs = response.block();
        return Arrays.stream(aphiaRefs)
                     .flatMap(children -> Arrays.stream(children))
                     .collect(Collectors.toList());
    }
}
