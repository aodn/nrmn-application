package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

public class WormsService implements AphiaRefService {

    private final WebClient wormsClient;

    @Autowired
    public WormsService(@Qualifier("wormsClient") WebClient wormsClient) {
        this.wormsClient = wormsClient;
    }

    @Override
    public List<AphiaRef> fuzzyNameSearch(String searchTerm) {
        Mono<List> response = wormsClient
                .get().uri(uriBuilder ->
                        uriBuilder.path("AphiaRecordsByMatchNames")
                                  .queryParam("scientificnames[]", searchTerm)
                                  .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List>(){});
        return response.block();
    }
}
